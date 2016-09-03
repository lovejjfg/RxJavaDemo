package com.lovejjfg.rxjavademo;

import android.content.SharedPreferences;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.f2prateek.rx.preferences.RxSharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.operators.OperatorToMultimap;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private   ArrayList<Student>  students;
    private RxSharedPreferences rxPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        rxPreferences = RxSharedPreferences.create(preferences);
        initStudents();
//        method1();
//        method2();
//        method3();

//        method4();

//        method11();

        method5();

    }

    private void method5() {
        Observable<String> netObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Log.e(TAG, "走网络了！！");
                subscriber.onNext("这是缓存数据！！");
                subscriber.onCompleted(); }
        }).doOnNext(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.e(TAG, "call: 保存数据到本地");
                rxPreferences.getString("cash").asAction().call(s);

            }
        });
        Observable<String> nativeObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (TextUtils.isEmpty(rxPreferences.getString("cash").get())) {
                    Log.e(TAG, "没有缓存，走网络！");
                    subscriber.onCompleted();
                } else {
                    Log.e(TAG, "有缓存！");
                    subscriber.onNext(rxPreferences.getString("cash").get());
                    subscriber.onCompleted();
                }
            }
        });

        Observable.concat(nativeObservable, netObservable)
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "完成了！");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "错误了！");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, s);
                    }
                });
    }

    private void method4() {
        final Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    Log.e("TAG1", "事件产生在: 主线程");
                } else {
                    Log.e("TAG1", "事件产生在: 子线程");
                }
                subscriber.onNext("a");
                subscriber.onNext("b");
                subscriber.onNext("c");
                subscriber.onNext("d");
                subscriber.onNext("e");
                subscriber.onCompleted();

            }
        });

        final Subscriber<String> stringSubscriber = new Subscriber<String>() {
            @Override
            public void onStart() {
                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    Log.e("TAG1", "onStart: 主线程");
                } else {
                    Log.e("TAG1", "onStart: 子线程");
                }

            }

            @Override
            public void onCompleted() {
                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    Log.e("TAG1", "onCompleted: 主线程");
                } else {
                    Log.e("TAG1", "onCompleted: 子线程");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ");
            }

            @Override
            public void onNext(String s) {
                if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                    Log.e("TAG1", "onNext: 主线程");
                } else {
                    Log.e("TAG1", "onNext: 子线程");
                }
                Log.e(TAG, "onNext: " + s);
            }
        };
        stringObservable
                .subscribeOn(Schedulers.io())//事件产生在哪个线程
                .subscribeOn(Schedulers.newThread())//事件产生在哪个线程
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        SystemClock.sleep(10000);

                        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                            Log.e("TAG1", "doOnSubscribe: 主线程");
                        } else {
                            Log.e("TAG1", "doOnSubscribe: 子线程");
                        }
                    }
                })
//                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())//事件消费在哪个线程
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.e("TAG1", "doOnUnsubscribe: 取消订阅了");
                        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                            Log.e("TAG1", "doOnUnsubscribe: 主线程");
                        } else {
                            Log.e("TAG1", "doOnUnsubscribe: 子线程");
                        }
                    }
                })
                .skip(2)
                .subscribe(stringSubscriber);
    }

    private void initStudents() {
        Student s1 = new Student(19, "xiaoqiang0");
        Student s2 = new Student(20, "xiaoqiang1");
        Student s3 = new Student(21, "xiaoqiang2");
        Student s4 = new Student(22, "xiaoqiang3");
        Student s5 = new Student(23, "xiaoqiang4");
        Student s6 = new Student(24, "xiaoqiang5");
        Student s7 = new Student(25, "xiaoqiang6");
        Student s8 = new Student(25, "xiaoqiang5");
        students = new ArrayList<>();
        students.add(s1);
        students.add(s2);
        students.add(s3);
        students.add(s1);
        students.add(s4);
        students.add(s5);
        students.add(s6);
        students.add(s7);
        students.add(s8);

    }

    private void method3() {
        Observable.from(students)
                .distinct()
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "doOnUnsubscribe!!! ");
                    }
                })
                .subscribe(new Action1<Student>() {
                    @Override
                    public void call(Student student) {
                        Log.e(TAG, "call: " + student);
                    }
                });
    }

    private void method2() {

        Observable.just(students)//创建Observable
                .flatMap(new Func1<ArrayList<Student>, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(ArrayList<Student> students) {
                        //变换为新的Observable
                        return Observable.from(students);
                    }
                })
                //过滤掉年龄和姓名相同的对象
                .distinct()
//                .take(1)
                //过滤掉年龄小于20的对象
                .filter(new Func1<Student, Boolean>() {
                    @Override
                    public Boolean call(Student student) {
                        return student.getAge() >= 20;
                    }

                })
                //将事件对象由Student 转换为 String
                .map(new Func1<Student, String>() {
                    @Override
                    public String call(Student student) {
                        return student.getName();
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "call: 取消订阅了！！");

                    }
                })
                //订阅
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext: " + s);
                    }
                });
    }

    private void method1() {
        Observable.just("S", "M", "s", "A", "I", "L")
                .distinct(new Func1<String, Object>() {
                    @Override
                    public Object call(String s) {
                        return s;
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.e(TAG, "onNext: " + s);
                    }
                });
    }
    private void method11() {
        Observable.just("A","A", "B", "C","C", "D", "E")
//                .map(new Func1<Student, Object>() {
//                })
                .distinct()
                .buffer(3)

//                .switchMap(new Func1<Student, Observable<?>>() {
//                    @Override
//                    public Observable<?> call(Student student) {
//                        return null;
//                    }
//                })
//                .flatMap(new Func1<Student, Observable<String>>() {
//                    @Override
//                    public Observable<String> call(Student student) {
//                        return null;
//                    }
//                })
//                .concatMap(new Func1<Student, Observable<String>>() {
//                    @Override
//                    public Observable<String> call(Student student) {
//                        return null;
//                    }
//                })
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> students) {
                        Log.e(TAG, "call: " + students);

                    }
                });
    }


}
