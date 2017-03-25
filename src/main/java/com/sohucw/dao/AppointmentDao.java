package com.sohucw.dao;

import com.sohucw.entity.Appointment;
import org.apache.ibatis.annotations.Param;

/**
 * Created by baidu on 17/3/13.
 */
public interface AppointmentDao {

    /**
     * 插入预约图书记录
     *
     * @param bookId
     * @param studentId
     * @return 插入的行数
     */
    int insertAppointment(@Param("bookId") long bookId, @Param("studentId") long studentId);

    /**
     * 通过主键查询预约图书记录，并且携带图书实体
     *
     * @param bookId
     * @param studentId
     * @return
     */
    Appointment queryByKeyWithBook(@Param("bookId") long bookId, @Param("studentId") long studentId);

/*
    提示：这里为什么要给方法的参数添加@Param注解呢？是因为该方法有两个或以上的参数，一定要加，不然mybatis识别不了。
    上面的BookDao接口的queryById方法和reduceNumber方法只有一个参数book_id，
    所以可以不用加 @Param注解，当然加了也无所谓~

    注意，这里不需要实现dao接口不用编写daoImpl， mybatis会给我们动态实现，但是我们需要编写相应的mapper。
在mapper目录里新建两个文件BookDao.xml和AppointmentDao.xml，分别对应上面两个dao接口，代码如下。

    */


}
