Windows系统下Git多个ssh-key的管理。随意百度下有好多文章，但是总是出现问题，clone、push代码出现：Error: Permission denied (publickey)。多数是config文件出现问题，然后 Host不正确。于是自己动手，记录下步骤，发布博客希望能帮助到更多人。


### 1、生成对应的私钥公钥
```
cd ~/.ssh
```
```
ssh-keygen -t rsa -f ~/.ssh/id_rsa_*** -C "******@163.com"
```
id_rsa_*** 为文件名称，"******@163.com" 是邮箱。  
然后提示密码，可以直接按回车ENTER，以后Push就不需要密码

同样的步骤添加其他账号。  
```
ssh-keygen -t rsa -f ~/.ssh/id_rsa_*** -C "******@**.com"
```

### 2、添加config文件：
config这个文件不是txt文本文件，没有后缀名，可以先复制id_rsa（不是id_rsa.pub）文件，再重命名为config，然后编辑；
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191021160256347.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2OTkyNjg4,size_16,color_FFFFFF,t_70)
```
# github key
Host github.com
    HostName github.com
    PreferredAuthentications publickey
    IdentityFile ~/.ssh/id_rsa_github
# gitlab key
Host *****
    HostName ******
    PreferredAuthentications publickey
    IdentityFile ~/.ssh/id_rsa
```

#### config文件：

name  | explanation
------------- | -------------
Host  | 即git@后面紧跟的名字，如：改为mygithub则：git clone git@mygithub:Shuguo/Anim.git
HostName  | 这个是真实的域名地址
IdentityFile  | 这里是id_rsa的地址
PreferredAuthentications  | 配置登录时用什么权限认证--可设为publickey,password publickey,keyboard-interactive等
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191021160136744.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2OTkyNjg4,size_16,color_FFFFFF,t_70)

在C:\Users\Administrator\.ssh目录下，打开id_rsa_\*****.pub，复制内容放入相应的平台配置ssh key  
如GitHub：  
点击右上方的Settings  
选择 SSH and GPG keys  
点击 New SSH key  
填写Title，自己随意写，然后将上面拷贝的~/.ssh/id_rsa\*****.pub文件内容粘帖到key一栏，在点击“Add SSH key”按钮就可以了。  
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191021160202813.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2OTkyNjg4,size_16,color_FFFFFF,t_70)
##### 测试，随意哪里打开git：ssh -T git@Host
```
ssh -T git@github.com
```
如，我的电脑：
```
Administrator@PC-201812211810 MINGW64 /c/Users
$ ssh -T git@github.com
Hi shuguoli68! You've successfully authenticated, but GitHub does not provide shell access.

Administrator@PC-201812211810 MINGW64 /c/Users
$ ssh -T git@git.qqxsapp.com
Welcome to GitLab, ***!

Administrator@PC-201812211810 MINGW64 /c/Users
$
```
![在这里插入图片描述](https://img-blog.csdnimg.cn/20191021160219740.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM2OTkyNjg4,size_16,color_FFFFFF,t_70)
成功！  
#### 全局配置账号：  
建议将常用的配置为全局的，不常用的配置为local
- 1、查看
```
git config --global --list
//或者
git config --global user.name
git config --global user.email
```
- 2、配置
```
git config --global user.name "shu"
git config --global user.email "******@163.com"
```
#### 仓库配置账号：  
不常用的配置为local，每次使用都必须先建立仓库，然后设置账号
- 1、查看
```
git config --local --list
//或者
git config --local user.name
git config --local user.email
```
- 2、配置
```
git config --local user.name "guo"
git config --local user.email "****@163.com"
```

比如：将GitHub账号设置为global，GitLab账号设置为local，那么GitLab上的项目提交代码必须设置一次账号，这个账号仅在该仓库下有效：
```
git config --local user.name "guo"
git config --local user.email "****@163.com"
```