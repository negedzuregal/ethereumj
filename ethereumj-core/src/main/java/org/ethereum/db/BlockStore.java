package org.ethereum.db;

import org.ethereum.core.Block;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * www.etherj.com
 *
 * @author: Roman Mandeleil
 * Created on: 12/11/2014 17:16
 */
@Repository
@Transactional(propagation= Propagation.SUPPORTS)
public class BlockStore {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    ApplicationContext ctx;


    @Transactional(readOnly = true)
    public Block getBlockByNumber(long blockNumber) {

        List result = sessionFactory.getCurrentSession().
                createQuery("from BlockVO where number = :number").
                    setParameter("number", blockNumber).list();

        if (result.size() == 0) return null;
        BlockVO vo = (BlockVO)result.get(0);

        return new Block(vo.rlp);
    }

    @Transactional(readOnly = true)
    public Block getBlockByHash(byte[] hash) {

        List result = sessionFactory.getCurrentSession().
                createQuery("from BlockVO where hash = :hash").
                setParameter("hash", hash).list();

        if (result.size() == 0) return null;
        BlockVO vo = (BlockVO)result.get(0);

        return new Block(vo.rlp);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<byte[]> getListOfHashesStartFrom(byte[] hash, int qty) {

        List<byte[]> hashes = new ArrayList<>();

        // find block number of that block hash
        Block block = getBlockByHash(hash);
        if (block == null) return hashes;

        List<byte[]> result = sessionFactory.getCurrentSession().
                createQuery("from BlockVO.hash where number >= :number").
                setParameter("number", block.getNumber()).
                setMaxResults(qty).list();

        for (byte[] h : result){
            hashes.add(h);
        }

        return hashes;
    }


    @Transactional
    public void saveBlock(Block block) {

        BlockVO blockVO =  new BlockVO(block.getNumber(), block.getHash(),
                block.getEncoded(), block.getCumulativeDifficulty());

        sessionFactory.getCurrentSession().persist(blockVO);
    }


    @Transactional(readOnly = true)
    public BigInteger getTotalDifficulty(){

        BigInteger result = (BigInteger)sessionFactory.getCurrentSession().
                createQuery("select sum(cummulativeDifficulty) from BlockVO").uniqueResult();

        return result;
    }


    @Transactional(readOnly = true)
    public Block getBestBlock(){

        Long bestNumber = (Long)
                sessionFactory.getCurrentSession().createQuery("select max(number) from BlockVO").uniqueResult();
        List result = sessionFactory.getCurrentSession().
                createQuery("from BlockVO where number = :number").setParameter("number", bestNumber) .list();

        if (result.isEmpty()) return null;
        BlockVO vo = (BlockVO)result.get(0);

        return new Block(vo.rlp);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Block> getAllBlocks() {

        List<BlockVO> result = sessionFactory.getCurrentSession().
                createQuery("from BlockVO").list();

        ArrayList<Block> blocks = new ArrayList<>();
        for (BlockVO blockVO : (List<BlockVO>)result){
            blocks.add(new Block(blockVO.getRlp()));
        }

        return blocks;
    }

    @Transactional
    public void reset() {
        sessionFactory.getCurrentSession().
              createQuery("delete from BlockVO").executeUpdate();
    }
}
