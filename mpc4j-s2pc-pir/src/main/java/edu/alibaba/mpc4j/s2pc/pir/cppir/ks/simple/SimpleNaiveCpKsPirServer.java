package edu.alibaba.mpc4j.s2pc.pir.cppir.ks.simple;

import com.google.common.primitives.Bytes;
import edu.alibaba.mpc4j.common.rpc.MpcAbortException;
import edu.alibaba.mpc4j.common.rpc.Party;
import edu.alibaba.mpc4j.common.rpc.PtoState;
import edu.alibaba.mpc4j.common.rpc.Rpc;
import edu.alibaba.mpc4j.common.structure.database.NaiveDatabase;
import edu.alibaba.mpc4j.common.structure.fusefilter.Arity3ByteFuseFilter;
import edu.alibaba.mpc4j.common.tool.crypto.hash.Hash;
import edu.alibaba.mpc4j.common.tool.crypto.hash.HashFactory;
import edu.alibaba.mpc4j.common.tool.utils.ObjectUtils;
import edu.alibaba.mpc4j.s2pc.pir.cppir.GaussianLweParam;
import edu.alibaba.mpc4j.s2pc.pir.cppir.index.simple.SimpleCpIdxPirConfig;
import edu.alibaba.mpc4j.s2pc.pir.cppir.index.simple.SimpleCpIdxPirServer;
import edu.alibaba.mpc4j.s2pc.pir.cppir.ks.AbstractCpKsPirServer;
import edu.alibaba.mpc4j.s2pc.pir.cppir.ks.HintCpKsPirServer;
import edu.alibaba.mpc4j.s2pc.pir.cppir.ks.simple.SimpleNaiveCpKsPirDesc.PtoStep;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.formula.functions.T;

import static edu.alibaba.mpc4j.s2pc.pir.cppir.ks.simple.SimpleNaiveCpKsPirDesc.PtoStep;
import static edu.alibaba.mpc4j.s2pc.pir.cppir.ks.simple.SimpleNaiveCpKsPirDesc.*;

/**
 * Simple naive client-specific preprocessing KSPIR server.
 *
 * @author Liqiang Peng
 * @date 2024/8/2
 */
public class SimpleNaiveCpKsPirServer<T> extends AbstractCpKsPirServer<T> implements HintCpKsPirServer<T> {
    /**
     * simple index PIR server
     */
    private final SimpleCpIdxPirServer simpleCpIdxPirServer;
    /**
     * arity
     */
    private int arity;

    public SimpleNaiveCpKsPirServer(Rpc serverRpc, Party clientParty, SimpleNaiveCpKsPirConfig config) {
        super(getInstance(), serverRpc, clientParty, config);
        GaussianLweParam gaussianLweParam = config.getGaussianLweParam();
        SimpleCpIdxPirConfig simpleCpIdxPirConfig = new SimpleCpIdxPirConfig.Builder(gaussianLweParam).build();
        simpleCpIdxPirServer = new SimpleCpIdxPirServer(serverRpc, clientParty, simpleCpIdxPirConfig);
        addSubPto(simpleCpIdxPirServer);
    }

    @Override
    public void init(Map<T, byte[]> keyValueMap, int l, int matchBatchNum) throws MpcAbortException {
        setInitInput(keyValueMap, l, matchBatchNum);
        logPhaseInfo(PtoState.INIT_BEGIN);

        //bff
        stopWatch.start();
        // concat Hash(key) and value
        // fingerprint.size() = 8
        Hash hash = HashFactory.createInstance(envType, DIGEST_BYTE_L);

        //将每一个key变成hash1(key),hash2(key),hash3(key)
        Map<T, byte[]> keyValueConcatMap = new HashMap<>();
        keyValueMap.keySet().forEach(key -> {
            byte[] digest = hash.digestToBytes(ObjectUtils.objectToByteArray(key));
            keyValueConcatMap.put(key, Bytes.concat(digest, keyValueMap.get(key)));
        });
        Arity3ByteFuseFilter<T> fuseFilter = new Arity3ByteFuseFilter<>(
            envType, keyValueConcatMap, byteL + DIGEST_BYTE_L, secureRandom
        );

        System.out.println("fuseFilter.filterLength(): " + fuseFilter.filterLength());


        arity = fuseFilter.arity();
        sendOtherPartyPayload(
            PtoStep.SERVER_SEND_FUSE_FILTER_SEED.ordinal(), Collections.singletonList(fuseFilter.seed())
        );
        stopWatch.stop();
        long initFuseFilterTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        stopWatch.reset();
        logStepInfo(PtoState.INIT_STEP, 1, 2, initFuseFilterTime, "Server generates fuse filter");

        // hint DB*A
        stopWatch.start();
        simpleCpIdxPirServer.init(NaiveDatabase.create((byteL + DIGEST_BYTE_L) * Byte.SIZE, fuseFilter.storage()));
        stopWatch.stop();
        long hintTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        stopWatch.reset();
        logStepInfo(PtoState.INIT_STEP, 2, 2, hintTime, "Server generates hints");

        logPhaseInfo(PtoState.INIT_END);
    }

    @Override
    public void pir(int batchNum) throws MpcAbortException {
        setPtoInput(batchNum);
        logPhaseInfo(PtoState.PTO_BEGIN);

        stopWatch.start();
        for (int i = 0; i < batchNum; i++) {
            answer();
        }
        stopWatch.stop();
        long responseTime = stopWatch.getTime(TimeUnit.MILLISECONDS);
        stopWatch.reset();
        logStepInfo(PtoState.PTO_STEP, 1, 1, responseTime, "Server responses query");

        logPhaseInfo(PtoState.PTO_END);
    }

    private void answer() throws MpcAbortException {
        for (int i = 0; i < arity; i++) {
            simpleCpIdxPirServer.answer();
        }
    }
}
