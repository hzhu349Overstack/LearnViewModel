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

可以看到屏幕的旋转、activity调用finish时ViewModel的生命周期一直是存活状态，当finish状态时可以理解为activity与ViewModel都
不需要使用了这时ViewMode销毁，然后回调onCleared方法。

基于ViewModel生命周期这个特点我们可以使用ViewModel来进行Fragment之间的数据共享。 ViewModel能将数据从Activity中剥离处理，只要Activity不销毁，ViewModel就一直存在，基于这些特性，多个Fragment 可以使用其 Activity 范围共享 ViewModel 来处理此类通信。

官方建议ViewModel不要持有Context引用，若是想要持有Context引用可以使用AndroidViewModel类，它继承自ViewModel，并接收Application作为Context，因此它的生命周期和应用Application的生命周期一样，不会导致内存泄漏，同时可以处理特定场景的数据问题。



###### 5、ViewModel与onSaveInstanceState()的对比

通常我们使用onSaveInstanceState()来解决屏幕旋转带来的数据丢失问题，但是它只能保存少量的支持序列化的数据，ViewModel支持页面中的所有数据。
需要注意的是，ViewModel不支持数据的持久化，当界面彻底销毁时，ViewModel及所持有的数据就不存在了，onSaveInstanceState()没有这个限制，可以持久化页面的数据，两者用途不一。

###### 6、ViewModel其他获取方式

首先需要添加依赖
```groovy
    implementation "androidx.fragment:fragment-ktx:1.5.0"
    implementation "androidx.activity:activity-ktx:1.5.0"
```
然后直接使用即可
```kotlin
//MainViewModel 为自定义的ViewModel
private val viewModel: MainViewModel by activityViewModels()
```

这样在ViewModel的生命周期内，ViewModel就可在Activity与Fragment中共享统一实例了。举个例子，很简单一个activity内添加两个fragment：

```kotlin
class NewsActivity : AppCompatActivity() {
    //viewModels()方法可接受自定义的ViewModelProvider.Factory对象，这样重写create方法可以提供自定义的viewModel对象。这里使用默认方式。
    private val mainViewModel1: MainViewModel by viewModels()
    private val mainViewModel2 by lazy {
        ViewModelProvider(this,ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        mainViewModel1.pageName = "Hello"

        Log.d("ViewModel","NewsActivity-mainViewModel1:${mainViewModel1}")
        Log.d("ViewModel","NewsActivity-mainViewModel2:${mainViewModel2}")
    }
}
```
```kotlin
class ListFragment : Fragment() {
    private val listViewModel1: MainViewModel by activityViewModels() // viewModel() 不可否则会产生新的对象
    private val listViewModel3: MainViewModel by activityViewModels()
    private val listViewModel2 by lazy { //不可用这种方案 会产生新的对象
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("ViewModel","ListFragment-listViewModel1:${listViewModel1}")
        Log.d("ViewModel","ListFragment-listViewModel2:${listViewModel2}")
        Log.d("ViewModel","ListFragment-listViewModel3:${listViewModel3}")
        return inflater.inflate(R.layout.fragment_list, container, false)
    }
}
```

```kotlin
class MenuFragment : Fragment() {

    private val menuViewModel1: MainViewModel by viewModels()
    private val menuViewModel2 by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("ViewModel","MenuFragment-menuViewModel1:${menuViewModel1}")
        Log.d("ViewModel","MenuFragment-menuViewModel2:${menuViewModel2}")
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }
}
```

log如下：

D/ViewModel: NewsActivity-mainViewModel1:news.MainViewModel@663ee90

D/ViewModel: NewsActivity-mainViewModel2:news.MainViewModel@663ee90

D/ViewModel: MenuFragment-menuViewModel1:news.MainViewModel@5d12f92

D/ViewModel: MenuFragment-menuViewModel2:news.MainViewModel@5d12f92

D/ViewModel: ListFragment-listViewModel1:news.MainViewModel@663ee90

D/ViewModel: ListFragment-listViewModel2:news.MainViewModel@e9ca389 // 注意这里获取的方式

D/ViewModel: ListFragment-listViewModel3:news.MainViewModel@663ee90

可见：

- 使用viewModels()或者ViewModelProvider方案获取ViewModel时activity中与Fragment中获取的实例是不同的。（对比NewsActivity与MenuFragment）

- 使用activityViewModels获取的ViewModel的实例是activity与fragment之间共享的实例。


###### 7、viewModels { viewModelFactory }的用法

这里结合Dagger综合运用下->

```kotlin
class MainViewModel @Inject constructor():ViewModel()
```

```kotlin
/**
 * Create by SunnyDay /12/07 10:07:15
 * 工具类:ViewModel工厂，自己实现ViewModel的创建
 */
class ViewModelFactory<T>@Inject constructor(
    private val modelProvider: Provider<T>)
    :ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = modelProvider.get() as T
}
```

```kotlin
@Component
interface ApplicationComponent {
     //产生MainViewModel
     fun getMainViewModelFactory():ViewModelFactory<MainViewModel>
     //这里还可以定义产生其他ViewModel的方法。
}
```

```kotlin
class DaggerBasicActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dagger_basic)

        val daggerContainer = (application as MyApplication).getDaggerContainer()
        // 获取ViewModel
        val mainModel:MainViewModel by viewModels {
          daggerContainer.getMainViewModelFactory()
        }
        println("获取ViewModel:$mainModel")
    }
}
```

###### 8、viewModels 与activityViewModels的获取区别


[官方文档](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)
