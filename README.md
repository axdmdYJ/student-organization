# 学生组织报名系统

## 数据库表设计

| **字段名**    | **数据类型**  | **描述**                                  |
| ------------- | ------------- | ----------------------------------------- |
| id            | bigint        | 主键id                                    |
| student_id    | VARCHAR(256)  | 学生学号(唯一索引）                       |
| name          | VARCHAR(256)  | 真实姓名                                  |
| username      | VARCHAR(256)  | 用户名                                    |
| password      | VARCHAR(256)  | 密码（加密存储）                          |
| qq            | VARCHAR(256)  | QQ 号                                     |
| phone         | VARCHAR(256)  | 手机号                                    |
| major         | VARCHAR(256)  | 专业和班级  |
| class_name    | varchar(256） | 班级                                      |
| wills         | VARCHAR(512)  | 志愿信息                                  |
| role          | tinyint(1)    | 角色 0: 学生  1: 管理员                ｜
| is_dispensing | bit(1)        | 是否服从调剂（使用 BOOLEAN 类型表示开关） |
| deletion_time | datetime      | 注销时间                                  |
| create_time   | datetime      | 创建时间                                  |
| update_time   | datetime      | 修改时间                                  |
| del_flag      | tinyint(1)    | 删除标识 0: 未删除 1: 已删除              |


## C端

### 用户注册

```json
// 测试样例01
{
    "username": "user01",
    "password": "123456"
}
{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}

// 测试样例02
{
    "username": "use",
    "password": "123456"
}
{
    "code": "B000205",
    "message": "用户账号不能低于4位",
    "data": null,
    "success": false
}

测试样例03
{
    "username": "use&&&&",
    "password": "123456"
}
{
    "code": "B000207",
    "message": "用户账号不能包含特殊字符",
    "data": null,
    "success": false
}
```

### 用户登陆

```json
测试样例01
{
    "username": "user02",
    "password": "123456"
}

{
    "code": "00000",
    "message": "对的",
    "data": "2569cec8-7070-4af2-9dd3-c65e9e87b963",
    "success": true
}

测试样例01
{
    "username": "user02",
    "password": ""
}

{
    "code": "B000204",
    "message": "账号或密码不能为空",
    "data": null,
    "success": false
}

测试样例02
{
    "username": "user02$$$",
    "password": "1233222"
}

{
    "code": "B000207",
    "message": "用户账号不能包含特殊字符",
    "data": null,
    "success": false
}

测试样例03
{
    "username": "user02",
    "password": "1233222"
}

{
    "code": "B000209",
    "message": "密码错误，请重试",
    "data": null,
    "success": false
}
```

### 提交报名信息

```json
测试样例01
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

{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}

测试样例02
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

{
    "code": "B000210",
    "message": "姓名格式错误",
    "data": null,
    "success": false
}
```

### 获取学生自己信息

```json
测试/registration-information接口
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

```json
测试样例1
{
    "username": "admin",
    "password": "123456"
}

{
    "code": "00000",
    "message": "对的",
    "data": {
        "token": "3cbfc8e2-0f60-4da0-9c31-77dffd889f40"
    },
    "success": true
}

测试样例2
{
    "username": "admin&&&&",
    "password": "123456"
}

{
    "code": "B000207",
    "message": "用户账号不能包含特殊字符",
    "data": null,
    "success": false
}

测试样例3
{
    "username": "admin",
    "password": "12345677"
}

{
    "code": "B000209",
    "message": "密码错误，请重试",
    "data": null,
    "success": false
}
```

### 管理员修改学生信息

```json
测试样例1
Header: 学生token
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
返回
{
    "code": "B000211",
    "message": "学生没有修改权限",
    "data": null,
    "success": false
}

测试样例2
Header: 管理员token
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
返回
{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}
```

### 重置学生密码

```json
测试样例1 管理员token
{
    "studentID": "20222022",
    "newPassword": "654321"
}
返回
{
    "code": "00000",
    "message": "对的",
    "data": null,
    "success": true
}

测试样例2 学生token

{
    "studentID": "20222022",
    "newPassword": "123564"
}

返回
{
    "code": "B000211",
    "message": "学生没有修改权限",
    "data": null,
    "success": false
}
```
