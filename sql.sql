delete from apply_form where 1;
alter table apply_form auto_increment = 1;

delete from experiment_project where 1;
alter table experiment_project auto_increment = 1;

delete from teacher where 1;
alter table teacher auto_increment = 1;

delete from course_serial where 1;
alter table course_serial auto_increment = 1;

delete from class_time where 1;
alter table class_time auto_increment = 1;

delete from course_name where 1;
alter table course_name auto_increment = 1;

delete from class where 1;
alter table class auto_increment = 1;

delete semester from semester where 1;
alter table semester auto_increment = 1;

delete laboratory_source from laboratory_source where 1;
alter table laboratory_source auto_increment = 1;

delete from laboratory where 1;
alter table laboratory auto_increment = 1;

delete teacher_suggest from teacher_suggest where 1;
alter table teacher_suggest auto_increment = 1;

create database file_tidy_db;

use file_tidy_db;

create table admin
(
    id       int auto_increment
        primary key,
    username varchar(255) not null,
    password varchar(255) not null,
    role     int         null comment '1：申请单位 2：审批单位',
    constraint username
        unique (username)
);

create index idx_username_password on admin(username, password);

create table class
(
    id         int auto_increment
        primary key,
    name       varchar(255) not null,
    people_Num int         not null,
    constraint name
        unique (name)
);

create index idx_name on class(name);

create table course_name
(
    id          int auto_increment
        primary key,
    course_name varchar(255)              not null,
    subject     varchar(4) default '0809' null comment '所属学科',
    constraint course_name_pk
        unique (course_name)
)
    comment '课程名称表';

create table laboratory
(
    id   int auto_increment
        primary key,
    name varchar(255) not null,
    constraint name
        unique (name)
);

create table semester
(
    id         int auto_increment
        primary key,
    start_date date not null,
    end_date   date not null,
    stage      int  not null,
    start_year int  not null,
    end_year   int  not null,
    check (`stage` in (1, 2))
);

create table teacher
(
    id        int auto_increment
        primary key,
    name      varchar(255) not null,
    username  varchar(255) null,
    password  varchar(255) null,
    telephone varchar(255) null,
    college   varchar(255) null,
    constraint username
        unique (username)
);

create index idx_username_password on teacher(username, password);

create table course_serial
(
    id             int auto_increment
        primary key,
    course_serial  varchar(255) null,
    course_name_id int          null,
    semester_id    int          null comment '学期id',
    constraint course_serial_ibfk_1
        foreign key (course_name_id) references course_name (id)
            on update cascade on delete cascade,
    constraint course_serial_ibfk_2
        foreign key (semester_id) references semester (id)
);

create table class_time
(
    id               int auto_increment
        primary key,
    weekday          int         null comment '星期',
    section          varchar(4)  null comment '节次',
    start_week       int         null comment '开始周次',
    end_week         int         null comment '结束周次',
    nature           varchar(255) null comment '性质',
    course_hour      int         null comment '课时',
    lab_id           int         null comment '实验室id',
    course_name_id   int         null comment '课程名称id',
    teacher_id       int         null comment '任课教师id',
    class_id         int         null comment '班级id',
    semester_id      int         null comment '学期id',
    constraint class_time_ibfk_1
        foreign key (lab_id) references laboratory (id)
            on update cascade on delete cascade,
    constraint class_time_ibfk_2
        foreign key (course_name_id) references course_name (id)
            on update cascade on delete cascade,
    constraint class_time_ibfk_3
        foreign key (teacher_id) references teacher (id)
            on update cascade on delete cascade,
    constraint class_time_ibfk_4
        foreign key (class_id) references class (id)
            on update cascade on delete cascade,
    constraint class_time_ibfk_5
        foreign key (semester_id) references semester (id)
            on update cascade on delete cascade,
    check (`weekday` in (1, 2, 3, 4, 5, 6, 7)),
    check (`section` in ('0102', '0304', '0506', '0708', '0910'))
)
comment '上课时间表';

create table experiment_project
(
    id                  int auto_increment
        primary key,
    experiment_content  varchar(255) not null comment '实验名称',
    experiment_no       varchar(255)  not null comment '实验序号',
    lab_id              int          not null comment '实验场地',
    course_name_id      int          not null comment '课程名称',
    experiment_category int          not null comment '实验类别',
    class_ids           varchar(255) not null comment '班级',
    experiment_people   int          not null comment '实验者人数',
    experiment_demand   int          not null comment '实验要求',
    experiment_type     int          not null comment '实验类型',
    experiment_hours    int          not null comment '实验学时数',
    teacher_id          int          not null comment '科任教师',
    semester_id         int          not null comment '学期',
    constraint experiment_project_ibfk_1
        foreign key (lab_id) references laboratory (id)
            on update cascade on delete cascade,
    constraint experiment_project_ibfk_2
        foreign key (course_name_id) references course_name (id)
            on update cascade on delete cascade,
    constraint experiment_project_ibfk_3
        foreign key (semester_id) references semester (id)
            on update cascade on delete cascade,
    constraint experiment_project_ibfk_4
        foreign key (teacher_id) references teacher (id)
            on update cascade on delete cascade
);

create table apply_form
(
    id                    int auto_increment
        primary key,
    used_lab_id           int                  not null comment '使用实验室ID',
    used_start_date       date                 not null comment '开始使用日期',
    used_week             text                 not null comment '使用周次',
    used_section          text                 not null comment '使用节次',
    sum_course_hours      int                  null comment '总课时',
    apply_reason          int                  not null comment '申请事由：2：调课 3：补课 4：培训 5：竞赛 6：考试 7：课程设计 8：其他',
    experiment_content    text                 not null comment '实验内容',
    course_name_id        int                  null comment '课程名称ID',
    class_id              int                  null comment '申请的班级',
    experiment_people     int                  not null comment '实验人数',
    applicant_id          int                  not null comment '申请人',
    applicant_telephone   varchar(255)          not null comment '申请人联系电话',
    applicant_college     varchar(255)          not null comment '申请人所在学院',
    submit_date           date                 not null comment '提交申请日期',
    state                 int default 0 null comment '申请状态',
    unit_opinion          text                 null comment '单位意见',
    unit_opinion_date     date                 null comment '单位意见日期',
    unit_opinion_name     varchar(255)          null comment '申请单位处理人姓名',
    approval_opinion      text                 null comment '院系审批意见',
    approval_opinion_date date                 null comment '院系审批意见日期',
    approval_opinion_name varchar(255)          null comment '审批单位处理人姓名',
    semester_id           int                  not null comment '学期ID',
    submit_teacher_id     int                  null comment '提交老师ID',
    constraint apply_form_applicant_fk
        foreign key (applicant_id) references teacher (id),
    constraint apply_form_course_name_id_fk
        foreign key (course_name_id) references course_name (id)
            on update cascade on delete cascade,
    constraint apply_form_ibfk_1
        foreign key (class_id) references class (id)
            on update cascade,
    constraint apply_form_lab_id_fk
        foreign key (used_lab_id) references laboratory (id)
            on update cascade on delete cascade,
    constraint apply_form_semester_id_fk
        foreign key (semester_id) references semester (id)
            on update cascade on delete cascade,
    check (`experiment_people` between 1 and 60)
);

create table laboratory_source
(
    id            int auto_increment
        primary key,
    lab_id        int        not null,
    week          int        null comment '周次',
    weekday       int        null,
    section       varchar(4) not null,
    state         int        null comment '实验室状态, 0: 空闲, 1: 正常上课, 2: 调课 3: 补课, 4: 培训, 5: 竞赛, 6: 考试, 7: 课程设计, 8: 其他',
    apply_form_id int        null,
    class_time_id int        null,
    semester_id   int        null,
    constraint apply_form_fk
        foreign key (apply_form_id) references apply_form (id)
            on update cascade on delete cascade,
    constraint class_time_fk
        foreign key (class_time_id) references class_time (id)
            on update cascade on delete cascade,
    constraint semester_fk
        foreign key (semester_id) references semester (id)
            on update cascade on delete cascade,
    check (`weekday` in (1, 2, 3, 4, 5, 6, 7)),
    check (`section` in ('0102', '0304', '0506', '0708', '0910'))
);

create view apply_form_view as
select `af`.`id`                    AS `apply_form_id`,
       `lab`.`name`                 AS `lab_name`,
       `af`.`used_start_date`       AS `used_start_date`,
       `af`.`used_week`             AS `used_week`,
       `af`.`used_section`          AS `used_section`,
       `af`.`sum_course_hours`      AS `sum_course_hours`,
       `af`.`apply_reason`          AS `apply_reason`,
       `cn`.`course_name`           AS `course_name`,
       `af`.`experiment_content`    AS `experiment_content`,
       `cls`.`name`                 AS `class_name`,
       `af`.`experiment_people`     AS `experiment_people`,
       `t`.`name`                   AS `applicant`,
       `af`.`applicant_telephone`   AS `applicant_telephone`,
       `af`.`applicant_college`     AS `applicant_college`,
       `af`.`submit_date`           AS `submit_date`,
       `af`.`unit_opinion`          AS `unit_opinion`,
       `af`.`unit_opinion_date`     AS `unit_opinion_date`,
       `af`.`unit_opinion_name`     AS `unit_opinion_name`,
       `af`.`approval_opinion`      AS `approval_opinion`,
       `af`.`approval_opinion_date` AS `approval_opinion_date`,
       `af`.`approval_opinion_name` AS `approval_opinion_name`,
       `af`.`semester_id`           AS `semester_id`,
       `af`.`submit_teacher_id`     AS `submit_teacher_id`,
       `af`.`state`                 AS `state`
from ((((`file_tidy_db`.`apply_form` `af` left join `file_tidy_db`.`laboratory` `lab`
         on ((`af`.`used_lab_id` = `lab`.`id`))) left join `file_tidy_db`.`teacher` `t`
        on ((`af`.`applicant_id` = `t`.`id`))) left join `file_tidy_db`.`class` `cls`
       on ((`af`.`class_id` = `cls`.`id`))) left join `file_tidy_db`.`course_name` `cn`
      on ((`af`.`course_name_id` = `cn`.`id`)));

create index idx_usedLabId_applicantId_classId_courseNameId on apply_form(used_lab_id, applicant_id, class_id, course_name_id);

create view class_schedule_view as
select `ct`.`id`                                                             AS `classTimeId`,
       `lab`.`name`                                                          AS `labName`,
       `cn`.`course_name`                                                    AS `courseName`,
       `tc`.`name`                                                           AS `teacherName`,
       concat(`ct`.`weekday`, `ct`.`section`)                                AS `weekdaySection`,
       concat('(', `ct`.`start_week`, '-', `ct`.`end_week`, '周)')           AS `startEndWeek`,
       `cls`.`name`                                                          AS `className`,
       `ct`.`nature`                                                         AS `courseNature`,
       `ct`.`course_hour`                                                    AS `sumCourseHour`,
       concat(`sem`.`start_year`, '-', `sem`.`end_year`, '-', `sem`.`stage`) AS `semester`
from `file_tidy_db`.`class_time` `ct`
    join `file_tidy_db`.`laboratory` `lab`
    join `file_tidy_db`.`course_name` `cn`
    join `file_tidy_db`.`class` `cls`
    join `file_tidy_db`.`teacher` `tc`
    join `file_tidy_db`.`semester` `sem`
where ((`ct`.`lab_id` = `lab`.`id`)
        and `ct`.`course_name_id` = `cn`.`id`)
        and `ct`.`teacher_id` = `tc`.`id`
        and `ct`.`class_id` = `cls`.`id`
        and `ct`.`semester_id` = `sem`.`id`;

create index idx_labId_courseNameId_teacherId_classId_semesterId on class_time(lab_id, course_name_id, teacher_id, class_id, semester_id);

create definer = root@localhost view experiment_project_view as
select `ep`.`id`                                                             AS `experiment_project_id`,
       concat(`cs`.`course_serial`, `ep`.`experiment_no`)                    AS `course_serial`,
       `ep`.`experiment_content`                                             AS `experiment_content`,
       `ep`.`experiment_no`                                                  AS `experiment_no`,
       `lab`.`name`                                                          AS `lab_name`,
       `cn`.`course_name`                                                    AS `course_name`,
       `ep`.`experiment_category`                                            AS `experiment_category`,
       `ep`.`class_ids`                                                      AS `class_ids`,
       `ep`.`experiment_people`                                              AS `experiment_people`,
       `ep`.`experiment_demand`                                              AS `experiment_demand`,
       `ep`.`experiment_type`                                                AS `experiment_type`,
       `ep`.`experiment_hours`                                               AS `experiment_hours`,
       `t`.`name`                                                            AS `teacher_name`,
       concat(`sem`.`start_year`, '-', `sem`.`end_year`, '-', `sem`.`stage`) AS `semester`
from `file_tidy_db`.`experiment_project` `ep`
         join `file_tidy_db`.`laboratory` `lab`
         join `file_tidy_db`.`course_name` `cn`
         join `file_tidy_db`.`teacher` `t`
         join `file_tidy_db`.`semester` `sem`
         join `file_tidy_db`.`course_serial` `cs`
where ((`ep`.`lab_id` = `lab`.`id`)
        and (`ep`.`course_name_id` = `cn`.`id`)
        and (`ep`.`teacher_id` = `t`.`id`)
        and (`ep`.`semester_id` = `sem`.`id`)
        and (`sem`.`id` = `cs`.`semester_id`)
        and (`cn`.`id` = `cs`.`course_name_id`))
group by `ep`.`id`;

create index idx_labId_courseNameId_teacherId_semesterId on experiment_project(lab_id, course_name_id, teacher_id, semester_id);
create index idx_semesterId_courseNameId on course_serial(semester_id, course_name_id);

drop table teacher_suggest;

create table teacher_suggest
(
    id         int primary key
        auto_increment,
    course_name_id       int          not null comment '课程名称id',
    teacher_id           int          not null comment '教师id',
    lab_id               int          not null comment '实验室id',
    semester_id          int          not null comment '学期id',
    environment_command  text         not null comment '环境需求',
    app_url              varchar(300) null comment '应用链接',
    submit_date          date         not null comment '提交日期',
    admin_reply          text         null comment '管理员回复',
    admin_reply_date     datetime     null comment '管理员回复日期',
    constraint teacher_suggest_ibfk_1
        foreign key (course_name_id) references course_name (id)
            on update cascade on delete cascade,
    constraint teacher_suggest_ibfk_2
        foreign key (teacher_id) references teacher (id)
            on update cascade on delete cascade,
    constraint teacher_suggest_ibfk_3
        foreign key (lab_id) references laboratory (id)
            on update cascade on delete cascade,
    constraint teacher_suggest_ibfk_4
        foreign key (semester_id) references semester (id)
            on update cascade on delete cascade
) comment '教师建议表';

drop view teacher_suggest_view;

create view teacher_suggest_view as
select ts.id, cn.course_name, t.name 'teacher_name', lab.name 'lab_name' , concat(sem.start_year, '-', sem.end_year, '-', sem.stage) semester, environment_command, app_url, submit_date, admin_reply, admin_reply_date
from teacher_suggest ts, course_name cn, teacher t, laboratory lab, semester sem
where ts.course_name_id = cn.id
    and ts.teacher_id = t.id
    and ts.lab_id = lab.id
    and ts.semester_id = sem.id;

create index idx_courseNameId_teacherId_labId_semesterId on teacher_suggest(course_name_id, teacher_id, lab_id, semester_id);
