package com.lovejjfg.rxjavademo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * Created by Joe on 2016/8/28.
 * Email lovejjfg@gmail.com
 */
public class Student  {
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Student && this.getAge() == ((Student) o).getAge() && this.getName().equals(((Student) o).getName());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{getAge(), getName()});
    }

    @Override
    public String toString() {
        return "Student{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private String name;
    private int age;

    public Student(int age, String name) {
        this.age = age;
        this.name = name;
    }


}
