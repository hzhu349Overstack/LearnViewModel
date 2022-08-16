# ViewModel

###### 1、ViewModel能够解决的问题

- 瞬态数据的丢失：如默认情况下的屏幕的屏幕旋转，界面重建导致一些数据丢失。
- 异步调用的内存泄漏：Activity退出后还有某些对象在做工作，任务未终止，对象不能及时释放。
- 类膨胀导致的维护难度和测试难度：代码都写在Activity中。

###### 2、ViewModel的作用

ViewModel 是介于View与Model之间的桥梁，可以使数据与视图分离。因此在开发中可以使用ViewModel帮助Activity分担一部分工作，
用于存放与界面相关的数据的，只要是界面上能看得到的数据，它的相关变量都应该存放在ViewModel中，而不是Activity中，这样可以在一定程度上减少Activity中的逻辑。

###### 3、简单使用

通过一个例子，来说明ViewModel的生命周期独立于activity，即使Activity销毁ViewModel依然存活。大致效果如下，每次点击按钮二者计数器+1，旋转屏幕则可验证结果。

![screenshot-test1.png](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/viewmodel/screenshot-test1.png)

```kotlin
/**
 * Create by SunnyDay /08/15 21:23:52
 */
class CommonData {
    var num = 0
}
```
```kotlin
/**
 * Create by SunnyDay /08/15 21:48:36
 */
class MainViewModel:ViewModel() {
    var number:Int = 0
    /**
     * ViewModel 提供的唯一一个可重写的方法。默认空实现。
     * 方法生命周期独立于Activity，当ViewModel于应用进程都不在使用时这个方法回调。可以简单理解为进程死了，这个方法就回调。
     * */
    override fun onCleared() {
        super.onCleared()
        Log.d("MainViewModel","onCleared")
    }
}
```
```kotlin
class MainActivity : AppCompatActivity() {
    private val commonData = CommonData()
    private val mainViewModel  by lazy {
        ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity","onCreate")
        /**
         * 给界面初始化初始值。注意ViewModel需要在这里试用下，否则达不到预期效果。
         */
        tv1.text = "CommonData:${commonData.num}"
        tv2.text =  "ViewModelData:${mainViewModel.number}"

        button.setOnClickListener {
            tv1.text = "CommonData:${++commonData.num}"
            tv2.text =  "ViewModelData:${++mainViewModel.number}"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity","onDestroy")
    }
}
```

点几下按钮，然后旋转频幕会发现，CommonData数据为0，ViewModelData数据不为0~

注意这里ViewModel对象的获取方式。首先构建一个ViewModelProvider，然后通过其get方法来获取ViewModel对象。get方法的参数很好理解就是自定义的ViewModel的class对象
ViewModelProvider的构造我们还是需要仔细看看：

```java
public class ViewModelProvider {
    /**
     * @param owner ViewModelStoreOwner是一个接口，Activity的父类ComponentActivity默认实现了这个接口，所以
     *              activity默认就是ViewModelStoreOwner。
     * @param factory 工厂类，用于创建ViewModel实例。            
     * */
    public ViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull Factory factory) {
        this(owner.getViewModelStore(), factory);
    }
}
```
ViewModel提供了一个内部类，默认实现了Factory，我们可以直接使用：

```java
    public static class NewInstanceFactory implements Factory {

        private static NewInstanceFactory sInstance;
        /**
         * 注意这里为包访问权限，我们不能直接使用。我们想要创建NewInstanceFactory实例直接new即可。
         * */
        @NonNull
        static NewInstanceFactory getInstance() {
            if (sInstance == null) {
                sInstance = new NewInstanceFactory();
            }
            return sInstance;
        }

        /**
         * 创建ViewModel实例，一般我们不使用这个，而是使用ViewModelProvider#get方法。
         * */
        @SuppressWarnings("ClassNewInstance")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection TryWithIdenticalCatches
            try {
                return modelClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot create an instance of " + modelClass, e);
            }
        }
    }
```
可见NewInstanceFactory#create方法比较鸡肋，若是使用这种方式创建ViewModel的实例，那么ViewModel是不能保存界面数据的，ViewModel的实例
每次都会重新创建，没有缓存实例的操作。因此需要使用ViewModelProvider#get方法来创建实例，这个方法中会通过ViewModelStore类来进行实例的缓存
复用。具体就不在分析了，点到为止，这是源码分析的事，嘿嘿嘿~

###### 4、ViewModel的生命周期

来看看官方给的一张图~

![ViewModel.png](https://gitee.com/sunnnydaydev/my-pictures/raw/master/github/viewmodel/viewmodel.png)


参考文章：https://cloud.tencent.com/developer/article/2034154

todo ：

- ViewModel的生命周期
- 与onSavedInstance的用途区别
- 其他获取方式



生命周期感知组件综合总结

[官方文档](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)