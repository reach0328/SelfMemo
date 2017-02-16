# **SelfMemo**

##**ORM**
- 객체 관계 매핑(Object-relational mapping; ORM)은 데이터베이스와 객체 지향 프로그래밍 언어 간의 호환되지 않는 데이터를 변환하는 프로그래밍 기법이다. 객체 관계 매핑 라고도 부른다. 객체 지향 언어에서 사용할 수 있는 "가상" 객체 데이터베이스를 구축하는 방법이다. 객체 관계 매핑을 가능하게 하는 상용 또는 무료 소프트웨어 패키지들이 있고, 경우에 따라서는 독자적으로 개발하기도한다.
- 오브젝트간에 관계를 연결해주는 개념으로 데이터베이스상에서의 테이블간의 매핑관계가 있는 것 처럼 ORM 또한 같은 정의를 내릴수 있다.
- ORM 프로그래밍 기법에 따른 프레임워크로 Hibernate, iBatis, Oracle TopLink와 같은 프레임워크가 등장했다


# Sqlite ORMLite Basic
Sqlite 데이터베이스를 ORM 툴을 이용해서 다루는 방법을 알아봅니다

## Query Basic
쿼리의 기본이되는 CRUD(Create[insert], Read[select], Update, Delete)
```text
-- 1. 테이블 생성하기
-- create table 테이블명 (컬럼명1 속성, 컬럼명2 속성);
create table bbs (
	bbsno int            -- 숫자는 int, float
	, title varchar(255) -- 숫자값 바이트의 문자열 입력시 사용
	, content text       -- 대용량의 데이터 입력시 사용
    );
-- 2. 데이터 입력하기
-- insert into 테이블명(컬럼명1, 컬럼명2) value(숫자값,'문자값');
INSERT INTO bbs (bbsno, title, content) VALUES(2, '타이틀', '내용입니다');
commit;
-- 3. 데이터 수정
-- update 테이블명 set 변경할컬럼명1 = 값, 컬럼명2 = 값 where 컬럼명 = 값
update bbs set title='이순신' where bbsno = 2;
commit;
-- 4. 데이터 삭제
-- delete from 테이블명 where 컬럼명 = 값;
delete from bbs where bbsno = 1;
commit;
-- 5. 데이터 읽기
-- select 불러올컬럼명1, 컬럼명2 from 테이블명 where 컬럼명 = 값
SELECT * FROM bbs;
-- 6. id 자동증가 테이블 생성하기
-- create table 테이블명 (컬럼명1 속성 autoincrement primary key, 컬럼명2 속성);
create table bbs2 (
	bbsno int primary key auto_increment not null -- 자동증가
	, title varchar(255) -- 숫자값 바이트의 문자열 입력시 사용
	, content text       -- 대용량의 데이터 입력시 사용
    , ndate datetime
    );

-- 자동증가 테이블에는 insert시에 값을 지정하지 않는다
INSERT INTO bbs2 (title, content) VALUES('타이틀', '내용입니다');
commit;
```

## ORM Basic
ORMLite 의 기본 사용법을 알아봅니다

### ORM Helper Class
ORMLite를 사용하기 위한 헬퍼 클래스 생성
```java
public class DBHelper extends OrmLiteSqliteOpenHelper {

    public static final String DB_NAME = "database.db"; // 데이터베이스 파일 이름
    public static final int DB_VERSION = 1;             // 데이터베이스 버전

    /**
     * 생성자 - 데이터베이스 파일 존재여부 검사를 하고 없으면 생성한다
     */
    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 생성자에서 호출되는 super(context... 에서 database.db 파일이 생성되어 있지 않으면 호출된다
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            // Bbs.class 파일에 정의된 테이블을 생성한다
            TableUtils.createTable(connectionSource, Bbs.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 생성자에서 호출되는 super(context... 에서 database.db 파일이 존재하지만 DB_VERSION 이 증가되면 호출된다
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            // Bbs.class 에 정의된 테이블 삭제한후
            TableUtils.dropTable(connectionSource, Bbs.class, false);
            // onCreate 를 통해 재생성한다
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

### Table Class
데이터베이스에서 사용할 테이블 클래스
```java
@DatabaseTable(tableName = "bbs") // 클래스에 매핑되는 테이블명 지정
public class Bbs {

    @DatabaseField(generatedId = true) // 필드로 선언 및 자동증가 옵션 추가
    private int id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String content;

    @DatabaseField
    private Date currentDate;

    // getters, setters 필요

    Bbs() {
        // 기본 생성자가 없으면 ormlite가 동작하지 않는다
    }

    public Bbs(String title, String content,Date currentDate){
        this.title = title;
        this.content = content;
        this.currentDate = currentDate;
    }
}
```

### ORM Connection
ORMLite를 통한 데이터베이스 연결
```java
// 데이터베이스 연결 - 헬퍼를 사용해서 싱글턴으로 생성할 수도 있다
DBHelper dbHelper = new dbHelper(this);

// 테이블 연결 - getDao 함수에 사용할 class 를 담아서 넘겨준다
Dao<Bbs, Long> bbsDao = dbHelper.getDao(Bbs.class);
```

### Create
데이터를 입력하는 Api
```java
bbsDao.create( new Bbs( "제목2", "내용2", new Date(System.currentTimeMillis()) ) );
```
### Read
데이터를 읽어오는 Api
```java
// 01. 조건 ID
Bbs bbs2 = bbsDao.queryForId(3L);
Log.i("Test Bbs one","queryForId :::::::::: content="+bbs2.getContent());

// 02. 조건 컬럼명 값
List<Bbs> bbsList2 = bbsDao.queryForEq("title", "제목3");
for(Bbs item : bbsList2){
    Log.i("Bbs Item","queryForEq :::::::::: id=" + item.getId() + ", title=" + item.getTitle());
}

// 03. 조건 컬럼 raw query - %문자열%
String query = "SELECT * FROM bbs where title like '%2%'";
GenericRawResults<Bbs> rawResults = bbsDao.queryRaw(query, bbsDao.getRawRowMapper());

List<Bbs> bbsList3 = rawResults.getResults();
for(Bbs item : bbsList3){
    Log.i("Bbs Item","queryRaw ::::::::: id=" + item.getId() + ", title=" + item.getTitle());
}

// 99. 전체쿼리
List<Bbs> bbsList = bbsDao.queryForAll();
for(Bbs item : bbsList){
    Log.i("Bbs Item","id=" + item.getId() + ", title=" + item.getTitle());
}
```
### Update
데이터를 수정하는 Api
```java
// 1. 수정할 데이터를 먼저 불러와서
Bbs bbsTemp = bbsDao.queryForId(7L);
// 2. 변경한 후
bbsTemp.setTitle("7번 수정됨");
// 3. 최종적으로 update를 통해 수정처리
bbsDao.update(bbsTemp);
```
### Delete
데이터를 삭제하는 Api
```java
// 1. 아이디로 삭제
bbsDao.deleteById(5L);
// 2. bbs 객체로 삭제
bbsDao.delete(bbs2);
```

###[Floatting Button](http://www.viralandroid.com/2016/02/android-floating-action-menu-example.html)
```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.1.0'
    compile 'com.android.support:design:23.1.0'

    compile 'com.github.clans:fab:1.6.2'
}
```

