package com.ywh.ywh_caffeine.mapper;

import com.ywh.ywh_caffeine.model.ToDoMsg;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *
 */
@Mapper
public interface CaffeineTestMapper {

    List<ToDoMsg> query(ToDoMsg toDoMsg);

}