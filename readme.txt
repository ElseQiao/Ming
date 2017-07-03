git@github.com:ElseQiao/Ming.git


git remote add origin https://git@github.com:ElseQiao/Ming.git

git set-url origin https://git@github.com:ElseQiao/ming.git


Warning: Permanently added the RSA host key for IP address '192.30.255.113' to the list of known hosts.
Permission denied (publickey).
fatal: Could not read from remote repository.

Please make sure you have the correct access rights
and the repository exists.

//settext()  加载长文字耗时问题
//cardview 背景设置，使用glide实现图片设置
//使用DiffUtil替代notifyDataSetChanged进行加载更多
//自定义LineEditText实现行线

//透明状态栏 与 EditText结合时的问题
  问题： 假如我们想要和其它界面一样得到现在的透明状态栏效果，即为toolbar设置android:fitsSystemWindows="true"
        这时候显示是正常的，可是一旦edittext获取焦点，软键盘弹出，那么将会出现toolbar被顶出屏幕（adjustpan直接
        平移）或者toolbar被拉伸占半个屏幕（adjustresize缩放界面）
   解决：不能在Toolbar设置fitsSystemWindows="true"，这样会使toolbar覆盖到状态栏，所以需要让系统自动给Toolbar
         设置paddingTop，我们可以自己手动给Toolbar 添加状态栏高度的paddingTop，在代码中利用反射获取状态栏高度

    衍生的新问题：此时toolbar显示正常，但是会发现edittext会被弹出的软键盘覆盖
               （此时软键盘的设置为adjustresize。至于为什么不用adjustpan，首先这个属性本来就不太合适，其次即使
                 使用了adjustpan套用scrollview的方式，依然会出现混乱状态，即toolbar时而会超出屏幕时而又不会）
          解决：给最外层view设置fitsSystemWindows="true"，但是这时状态栏会被设置padding，所以透明状态栏的效果
                又没了，这是我们只要重写外层view的fitSystemWindows的方法，把padding值改为0即可（参见
                SoftInputRelativeLayout）

