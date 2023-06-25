package com.example.exrxjava;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.exrxjava.databinding.ActivityMainBinding;
import com.jakewharton.rxbinding4.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

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

    private Disposable disposable;
    private ActivityMainBinding activityMainBinding;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());


        fromView(activityMainBinding.edtSearch)
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> Log.d("beforeTextChanged", data)
                );

        useZip();
    }


    private void useZip(){
        Observable.zip(Observable.fromArray(getListUsers1()),
                Observable.fromArray(getListProduct1()),
                (dataUser , dataProduct)->{
                    List<Object> objectList = new ArrayList<>();
                    for (User user: dataUser
                         ) {
                        objectList.add(user);
                    }
                    for (Product product: dataProduct
                         ) {
                        objectList.add(product);
                    }
                    return objectList;
                }
        ).map(data -> {
                    for (Object object : data
                         ) {
                        if (object instanceof User){
                            ((User) object).setName(((User) object).getName()+" + 1");
                        }
                        else if (object instanceof Product){
                            ((Product) object).setNameProduct(((Product) object).getNameProduct()+" + 2");

                        }
                    }
                    return data;
        })
                .delay(4000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Object>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d ;
                    }

                    @Override
                    public void onNext(@NonNull List<Object> objects) {
                        Log.d("TAG", objects+" ");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void useFlagMap(){
        Observable<List<User>> observable = Observable.fromArray(getListUsers1())
                .flatMap(data ->
                        {
                            for (User user : data) {
                                user.setName(" + A");
                            }
                            return Observable.just(data);
                        }
                )
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


        observable.subscribeWith(new Observer<List<User>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
                Log.d("TAG","onSubscribe");

            }

            @Override
            public void onNext(@NonNull List<User> users) {
                Log.d("TAG",users+" ");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("TAG",e.getMessage());

            }

            @Override
            public void onComplete() {
                Log.d("TAG","onComplete");

            }
        });

    }

    private void throttleFirst() {
        RxView.clicks(activityMainBinding.btnSearch)
                .throttleFirst(4000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> Log.d("TAG", "Click " + System.currentTimeMillis()),
                        error -> Log.d("TAG", "Error " + error.getMessage())
                );
    }

    private void throttleLast() {

        RxView.clicks(activityMainBinding.btnSearch)
                .throttleLast(4000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> Log.d("TAG", "Click " + System.currentTimeMillis()),
                        error -> Log.d("TAG", "Error " + error.getMessage())

                );
    }

    private void throttleLatest() {
        RxView.clicks(activityMainBinding.btnSearch)
                .throttleLatest(4000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> Log.d("TAG", "Click " + System.currentTimeMillis()),
                        error -> Log.d("TAG", "Error " + error.getMessage())

                );
    }

    private List<User> getListUsers1() {
        List<User> userList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            userList.add(new User(i, "User " + i));
        }

        return userList;
    }


    private List<Product> getListProduct1() {
        List<Product> productList = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            productList.add(new Product(i, "Product " + i));
        }
        return productList;
    }


    private Observable<String> fromView(EditText editText) {
        PublishSubject<String> publishSubject = PublishSubject.create();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Log.d("beforeTextChanged", String.valueOf(charSequence));
                publishSubject.onNext(String.valueOf(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return publishSubject;
    }

    private Observer<User> getObserverUser() {
        return new Observer<User>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposable = d;
                Log.e("RXJAVA:", "onSubscrive");
            }

            @Override
            public void onNext(@NonNull User user) {
                Log.e("RXJAVA:", user + " ");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("RXJAVA:", e.getMessage() + " ");
            }

            @Override
            public void onComplete() {
                Log.e("RXJAVA:", "onComplete");
            }
        };
    }

    private Observable<User> getObservableUsers() {
        List<User> getListUsers = getListUsers();

        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<User> emitter) throws Throwable {
                for (User user : getListUsers) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(user);
                    }
                }
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<User> getListUsers() {
        List<User> result = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            result.add(new User(i, "Name " + i));
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        if (disposable != null) disposable.dispose();
        super.onDestroy();
    }
}