package com.example.exrxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// Khái niệm về Thread
// Main thread , UI thread là gì ?
// Khi hệ thống android bắt đầu chạy 1 ứng dụng thì hệ thống sẽ start 1 thread ban đầu  và 1 process. Thread ban đầu này
// là MainThread.

// Ui Thread
// Bởi có 2 lý do, Thread này gửi các sự kiên đến widget, tức là đến các view ở giao diện điện thoại, thậm chỉ các sữ
// kiện vẽ

// Ngoài ra Thread này cũng phải tương tác với bộ công cụ Android UI

// Khi nào mà Main Thread lại không được gọi là UI Thread không?. Đó là khi một chương trình có nhiều hơn 1
// thread phụ trách việc xử lý gia odieenj

// Worker thread, Background Thread

// Các thành phần cơ bản của Rx là

// Observable, Observer, Schedulers, Operators, Subscription
// Observable: sẽ thực hiện một số công việc và sẽ phát dữ liệu đi
// Observer : là thằng không thể thiếu nó sẽ đi liên với observable, no sẽ lắng nghe nhận dữ liệu mà thằng observable phát ra
// schedulers: là có nhiệm vụ xác định luồng cho thằng observable sẽ thực thi tại luồng nào và luồng cho thằng observer nhận dữ liệu
// tại luồng nào
// Operators: là thằng có thể thay đổi dữ liệu từ thằng obserable phát ra trước khi dữ liệu đến thằng observer
// subscription: là thằng có nhiệm vụ liên kết giữa thằng observable và observer
// disposable: Dùng để hủy sự kết nối của observer vs observable
public class MainActivity extends AppCompatActivity {

    private Disposable disposable ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<User> observable = getObservableUsers();

        Observer<User> observer = getObserverUser();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);



    }

    private Observer<User> getObserverUser(){
        return new Observer<User>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
                Log.e( "RXJAVA:", "onSubscrive" );
            }

            @Override
            public void onNext(@NonNull User user) {
                Log.e( "RXJAVA:", user+" " );
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e( "RXJAVA:", e.getMessage()+" " );
            }

            @Override
            public void onComplete() {
                Log.e( "RXJAVA:", "onComplete" );
            }
        };
    }
    private Observable<User> getObservableUsers() {
        List<User> getListUsers = getListUsers() ;

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<User> emitter) throws Throwable {
                for (User user: getListUsers) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(user);
                    }
                }
                if (!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    private List<User> getListUsers(){
        List<User> result = new ArrayList<>();
        for(int i = 1; i <= 5 ; i++) {
            result.add(new User(i, "Name "+ i));
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }
}