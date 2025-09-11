# MyCloud项目
## table
1. CREATE TABLE user (
   name VARCHAR(64) PRIMARY KEY,
   password VARCHAR(128),
   limit_volume INT,
   dir_name CHAR(32),
   create_date DATETIME
   );
2. CREATE TABLE login_tokens (
   name VARCHAR(64),  
   login_token CHAR(255) PRIMARY KEY,  
   create_date DATETIME,  
   FOREIGN KEY (name) REFERENCES user(name)  
   );
3. create table ip_black_list(
   ip varchar(39) primary key
   );
4. CREATE TABLE login_log (
   id INT AUTO_INCREMENT PRIMARY KEY,   
   name VARCHAR(64),                 
   login_time DATETIME,              
   login_ip varchar(39),          
   FOREIGN KEY (name) REFERENCES user(name)    
   );
5. 

## function
### 1.login 
1.登陆  
2.十天免密 
3.注册（后期添加）  
### 2.home  
1.显示文件和文件夹  
1.1文件夹名/文件名  
1.2最后修改时间  
1.3大小（MB）  
2.修改密码  
3.前往后台  
4.上传文件  
5.下载文件  
6.删除文件  
7.删除文件夹  
8.创建文件夹  
9.安全退出  
### 3.backend  
1.返回首页  
2.访问控制  
2.1ipv4/v6黑名单管理（格式127.0.0.1和1111:1111:1111:1111:1111:1111）  
2.2UA黑名单（后期添加）   
3.登陆日志  
4.用户管理   
4.1创建用户   
4.2删除用户   
4.3设置最大容量（默认1GB）   
4.4修改其他用户密码    
4.5查看用户密码  
5.安全退出  
## 系统初始化
1.初始化管理员配置信息，存储到ServletContext中，admin_name和admin_password    
2.初始化存储配置，存储到ServletContext中，file_store_path      
3.初始化表，根据配置信息，初始化表和初始化存储  
4.初始化定时任务，清理数据库不需要的内容  

## 登陆状态记录
1.十天免登录用使用Cookie+DB  
2.session会话保持60分钟免登陆，记录user_name  
3.session记录用户文件存储根目录user_directory  