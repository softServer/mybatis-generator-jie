package com.ybjdw.base.mybatis.util;


import java.util.List;

public interface DataMapper<T, E, K> {

    long countByExample(E example);

    int deleteByExample(E example);

    int deleteByPrimaryKey(K id);

    int insert(T record);

    int insertSelective(T record);

    List<T> selectAll();

    int updateByPrimaryKey(T record);
}
