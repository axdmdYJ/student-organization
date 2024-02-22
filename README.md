# 学生组织报名系统

- Host: 127.0.0.1
- port:8008

## 环境依赖
RabbitMQ
SpringBoot 3
MyBatis Plus 3.5
MySQL 8.0
Redis

## 目录结构
```
student-organization
    ├── README.md
    ├── doc
    ├── pom.xml
    ├── src
    │   ├── main
    │   │   ├── java
    │   │   │   └── com
    │   │   │       └── tjut
    │   │   │           └── zjone
    │   │   │               ├── StudentOrganizationApplication.java
    │   │   │               ├── common
    │   │   │               │   ├── biz
    │   │   │               │   │   └── user
    │   │   │               │   │       ├── UserContext.java
    │   │   │               │   │       ├── UserInfoDTO.java
    │   │   │               │   │       └── UserTransmitFilter.java
    │   │   │               │   ├── constant
    │   │   │               │   │   └── RoleConstant.java
    │   │   │               │   ├── convention
    │   │   │               │   │   ├── errorcode
    │   │   │               │   │   │   ├── BaseErrorCode.java
    │   │   │               │   │   │   └── IErrorCode.java
    │   │   │               │   │   ├── exception
    │   │   │               │   │   │   ├── AbstractException.java
    │   │   │               │   │   │   ├── ClientException.java
    │   │   │               │   │   │   ├── RemoteException.java
    │   │   │               │   │   │   └── ServiceException.java
    │   │   │               │   │   └── result
    │   │   │               │   │       ├── Result.java
    │   │   │               │   │       └── Results.java
    │   │   │               │   ├── enums
    │   │   │               │   │   ├── RoleEnum.java
    │   │   │               │   │   └── UserErrorCodeEnum.java
    │   │   │               │   └── web
    │   │   │               │       └── GlobalExceptionHandler.java
    │   │   │               ├── config
    │   │   │               │   ├── MyMetaObjectHandler.java
    │   │   │               │   └── MybatisPlusAutoConfiguration.java
    │   │   │               ├── controller
    │   │   │               │   └── UserController.java
    │   │   │               ├── dao
    │   │   │               │   ├── entity
    │   │   │               │   │   ├── UserDO.java
    │   │   │               │   │   └── WillInfo.java
    │   │   │               │   └── mapper
    │   │   │               │       └── UserMapper.java
    │   │   │               ├── dto
    │   │   │               │   ├── req
    │   │   │               │   │   ├── AdminUpdateDTO.java
    │   │   │               │   │   ├── UserLoginReqDTO.java
    │   │   │               │   │   ├── UserPutRegReqDTO.java
    │   │   │               │   │   ├── UserPwdResetReqDTO.java
    │   │   │               │   │   └── UserRegisterReqDTO.java
    │   │   │               │   └── resp
    │   │   │               │       ├── AdminGetInfoRespDTO.java
    │   │   │               │       ├── UserGetInfoRespDTO.java
    │   │   │               │       ├── UserLoginRespDTO.java
    │   │   │               │       └── UserPageRespDTO.java
    │   │   │               ├── service
    │   │   │               │   ├── UserService.java
    │   │   │               │   └── impl
    │   │   │               │       └── UserServiceImpl.java
    │   │   │               └── util
    │   │   │                   └── FormatVerifyUtil.java
    │   │   └── resources
    │   │       ├── application.yaml
    │   │       └── mapper
    │   │           └── UserMapper.xml
    │   └── test
    │       └── java
    │           └── com
    │               └── tjut
    │                   └── zjone
    │                       └── StudentOrganizationApplicationTests.java

```
## 数据库表设计

| **字段名**    | **数据类型**  | **描述**                     |
| ------------- | ------------- | ---------------------------- |
| id            | bigint        | 主键id                       |
| student_id    | varchar(256)  | 学生学号(唯一索引）          |
| name          | varchar(256)  | 真实姓名                     |
| username      | varchar(256)  | 用户名                       |
| password      | varchar(256)  | 密码（加密存储）             |
| qq            | varchar(256)  | QQ 号                        |
| phone         | varchar(256)  | 手机号                       |
| major         | varchar(256)  | 专业                         |
| class_name    | varchar(256） | 班级                         |
| wills         | varchar(512)  | 志愿信息                     |
| role          | tinyint(1)    | 角色 0: 学生 1: 管理员       |
| is_dispensing | bit(1)        | 是否服从调剂                 |
| deletion_time | datetime      | 注销时间                     |
| create_time   | datetime      | 创建时间                     |
| update_time   | datetime      | 修改时间                     |


解决SpringBoot3.0和mp冲突问题
```java
<dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>3.5.5</version>
</dependency>
```

## C端

### 用户注册

- 样例1:

Param：

```json
{
    "username": "user01",
    "password": "123456"
}
```

Response:

```json
{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}
```

- 样例2

Param:

```json
{
    "username": "use",
    "password": "123456"
}
```

Response:

```json
{
    "code": "B000205",
    "message": "用户账号不能低于4位",
    "data": null,
    "success": false
}

```

- 样例3

Param

```json
{
    "username": "use&&&&",
    "password": "123456"
}
```

Response:

```json
{
    "code": "B000207",
    "message": "用户账号不能包含特殊字符",
    "data": null,
    "success": false
}
```

### 用户登陆

- 样例1

Param：

```json
{
    "username": "user02",
    "password": "123456"
}
```

Response：

```json
{
    "code": "00000",
    "message": "对的",
    "data": "2569cec8-7070-4af2-9dd3-c65e9e87b963",
    "success": true
}
```

- 样例2

Param：

```json
{
    "username": "user02",
    "password": ""
}
```

Response:

```json
{
    "code": "B000204",
    "message": "账号或密码不能为空",
    "data": null,
    "success": false
}
```

- 样例3

Param:

```json
{
    "username": "user02",
    "password": "1233222"
}

```

Response:

```json
{
    "code": "B000209",
    "message": "密码错误，请重试",
    "data": null,
    "success": false
}
```

### 提交报名信息

- 样例1

Param：

```json
{
    "studentID": "20222022",
    "name": "咩酱",
    "qq": "1120222022",
    "phone": "18100000006",
    "major": "计算机科学与技术",
    "className": "一班",
    "wills": [
        {
            "organization": "科技协会",
            "department": "科技协会",
            "reason": "我要学技术！"
        },
        {
            "organization": "学生会",
            "department": "学习部",
            "reason": "学生会厉害！"
        }
    ],
    "isDispensing" : true
}



```

Response:

```json
{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}
```

- 样例2

Param

```json
{
    "studentId": "20222023",
    "name": "咩酱2",
    "qq": "1120222022",
    "phone": "18100000006",
    "major": "计算机科学与技术",
    "className": "二班",
    "wills": [
        {
            "organization": "科技协会",
            "department": "科技协会",
            "reason": "我要学技术！"
        },
        {
            "organization": "学生会",
            "department": "学习部",
            "reason": "学生会厉害！"
        }
    ],
    "isDispensing" : 0
}

```

Response:

```json
{
    "code": "B000210",
    "message": "姓名格式错误",
    "data": null,
    "success": false
}
```

### 获取学生自己信息

- 样例

Header: 学生token

```json
{
    "code": "00000",
    "message": "对的",
    "data": {
        "studentID": "20222066",
        "name": "咩酱在哪",
        "qq": "1120222022",
        "phone": "18100000006",
        "major": "计算机科学与技术",
        "className": "一班",
        "wills": "[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]",
        "isDispensing": true
    },
    "success": true
}
```

## B端

### 管理员登录

- 样例1

Param：

```json
{
    "username": "admin",
    "password": "123456"
}
```

Response：

```json
{
    "code": "00000",
    "message": "对的",
    "data": "2569cec8-7070-4af2-9dd3-c65e9e87b963",
    "success": true
}
```

- 样例2

Param：

```json
{
    "username": "admin",
    "password": ""
}
```

Response:

```json
{
    "code": "B000204",
    "message": "账号或密码不能为空",
    "data": null,
    "success": false
}
```

- 样例3

Param:

```json
{
    "username": "admin",
    "password": "1233222"
}

```

Response:

```json
{
    "code": "B000209",
    "message": "密码错误，请重试",
    "data": null,
    "success": false
}
```

### 管理员修改学生信息

- 样例1

Header: 管理员token
Param：

```json
{
    "studentID": "20222022",
    "name": "咩酱修改测试",
    "qq": "1120222022",
    "phone": "18100000006",
    "major": "计算机科学与技术",
    "className": "一班",
    "wills": [
        {
            "organization": "科技协会",
            "department": "科技协会",
            "reason": "我要学技术！"
        },
        {
            "organization": "学生会",
            "department": "学习部",
            "reason": "学生会厉害！"
        }
    ],
    "isDispensing" : true
}
```

Response:

```json
{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}
```

- 样例2

Header：学生token
Param：

```json
{
    "studentID": "20222022",
    "name": "咩酱修改测试",
    "qq": "1120222022",
    "phone": "18100000006",
    "major": "计算机科学与技术",
    "className": "一班",
    "wills": [
        {
            "organization": "科技协会",
            "department": "科技协会",
            "reason": "我要学技术！"
        },
        {
            "organization": "学生会",
            "department": "学习部",
            "reason": "学生会厉害！"
        }
    ],
    "isDispensing" : true
}
```

Response:

```json
{
    "code": "B000211",
    "message": "学生没有修改权限",
    "data": null,
    "success": false
}
```

### 重置学生密码

- 样例1

Header：管理员token
Param:

```json
{
    "studentID": "20222022",
    "newPassword": "654321"
}
```

Response:

```json
{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}
```

- 样例2

Header：学生token
Param：

```json
{
    "studentID": "20222022",
    "newPassword": "123564"
}
```

Response:

```json
{
    "code": "B000211",
    "message": "学生没有修改权限",
    "data": null,
    "success": false
}
```

### 管理员获取自己信息

- 样例

Header： 管理员token
Response:

```json
{
    "code": "00000",
    "message": "对的",
    "data": {
        "username": "admin",
        "role": "管理员"
    },
    "success": true
}
```

### 获取用户信息

| **参数名** | **参数值** | **类型** | **说明**               |
| ---------- | ---------- | -------- | ---------------------- |
| pageNum    | 默认：1    | integer  | 第几页                 |
| pageSize   | 默认：10   | integer  | 每页大小               |
| keyword    | 默认：null | string   | 关键字（用于模糊查询） |

- 样例1：

Response：

```json
{
    "code": "00000",
    "message": "对的",
    "data": {
        "records": [
            {
                "studentID": "20222022",
                "name": "咩酱修改测试",
                "qq": "1120222022",
                "phone": "18100000006",
                "major": "计算机科学与技术",
                "className": "一班",
                "wills": "[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]",
                "isDispensing": true
            },
            {
                "studentID": "20222023",
                "name": "咩酱酱",
                "qq": "1120222032",
                "phone": "18100010016",
                "major": "物联网",
                "className": "一班",
                "wills": "[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]",
                "isDispensing": false
            },
            {
                "studentID": "20222025",
                "name": "咩酱酱",
                "qq": "1120222032",
                "phone": "18100010016",
                "major": "大数据",
                "className": "一班",
                "wills": "[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]",
                "isDispensing": true
            },
            {
                "studentID": "20222024",
                "name": "咩酱咩",
                "qq": "1120222032",
                "phone": "18100010016",
                "major": "中加",
                "className": "一班",
                "wills": "[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]",
                "isDispensing": true
            }
        ],
        "total": 0,
        "size": 10,
        "current": 1,
        "pages": 0
    },
    "success": true
}
```

- 样例2

header：管理员token

```json
{
    "code": "00000",
    "message": "对的",
    "data": {
        "records": [
            {
                "studentID": "20222022",
                "name": "咩酱修改测试",
                "qq": "1120222022",
                "phone": "18100000006",
                "major": "计算机科学与技术",
                "className": "一班",
                "wills": "[{\"department\":\"科技协会\",\"organization\":\"科技协会\",\"reason\":\"我要学技术！\"},{\"department\":\"学习部\",\"organization\":\"学生会\",\"reason\":\"学生会厉害！\"}]",
                "isDispensing": true
            }
        ],
        "total": 0,
        "size": 10,
        "current": 1,
        "pages": 0
    },
    "success": true
}
```
