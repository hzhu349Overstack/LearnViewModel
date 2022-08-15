# ViewModel

###### 1、ViewModel能够解决的问题

- 瞬态数据的丢失：如默认情况下的屏幕的屏幕旋转，界面重建导致一些数据丢失。
- 异步调用的内存泄漏：Activity退出后还有某些对象在做工作，任务未终止，对象不能及时释放。
- 类膨胀导致的维护难度和测试难度：代码都写在Activity中。

###### 2、ViewModel的作用

ViewModel 是介于View与Model之间的桥梁，可以使数据与视图分离。因此在开发中可以使用ViewModel帮助Activity分担一部分工作，
用于存放与界面相关的数据的，只要是界面上能看得到的数据，它的相关变量都应该存放在ViewModel中，而不是Activity中，这样可以在一定程度上减少Activity中的逻辑。

###### 3、简单使用



-----------------
参考文章：https://cloud.tencent.com/developer/article/2034154

todo ：

- ViewModel的生命周期
- 与onSavedInstance的用途区别
- 其他获取方式



生命周期感知组件综合总结

[官方文档](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)