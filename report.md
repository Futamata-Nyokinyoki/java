# プログラミング応用　プログラミング技術　レポート

## グループ 16 　ふたまたニョキニョキ㌠

### メンバー

- 33114070 田内 智哉
- 33114098 野上 恵吾
- 33114113 藤岡 拓夢

---

## はじめに

本レポートでは、主にデータベースを用いつつ、JUnit を用いたテスト、BoF のデザインパターン、Java 17 にて実装された新機能を取り込んだシステムを作成した。

私たちが作成したのは、名工大で現在使われている打刻システム「ピロリン」や履修登録システムを再現した「ニョキニョキピロリン」である。<br>
「ニョキニョキピロリン」は以下に示すような機能を持っている。

- 履修登録
- 打刻
- 出欠判定

機能ごとに分け、それぞれのおおまかな実装方法を説明するとともに、適宜条件となっていたデザインパターン、Java 17 の新機能について触れたのち、テストについて述べる。

## ディレクトリ構成

以下に今回私たちが作成・利用した各種ファイルの場所を示す。

java-nyokinyoki/<br>
&emsp;├lib/<br>
&emsp;│&ensp;├hamcrest-core-1.3.jar<br>
&emsp;│&ensp;├jstl-api-1.2.jar<br>
&emsp;│&ensp;├jstl-impl-1.2.jar<br>
&emsp;│&ensp;├junit-4.13.2.jar<br>
&emsp;│&ensp;└sqlite-jdbc-3.41.2.1.jar<br>
&emsp;├src/<br>
&emsp;│&ensp;├main/java/com/nyokinyoki/<br>
&emsp;│&ensp;│&emsp;├TimeTable/<br>
&emsp;│&ensp;│&emsp;│&emsp;├Course/<br>
&emsp;│&ensp;│&emsp;│&emsp;│&emsp;├TimeSlot/<br>
&emsp;│&ensp;│&emsp;│&emsp;│&emsp;│&emsp;├TimeSlot.java<br>
&emsp;│&ensp;│&emsp;│&emsp;│&emsp;│&emsp;└TimeSlotDAO.java<br>
&emsp;│&ensp;│&emsp;│&emsp;│&emsp;├Course.java<br>
&emsp;│&ensp;│&emsp;│&emsp;│&emsp;└CourseDAO.java<br>
&emsp;│&ensp;│&emsp;│&emsp;├TimeTable.java<br>
&emsp;│&ensp;│&emsp;│&emsp;├TimeTableDAO.java<br>
&emsp;│&ensp;│&emsp;│&emsp;└TimeTableIntegrationTest.java<br>
&emsp;│&ensp;│&emsp;├AbstractDAO.java<br>
&emsp;│&ensp;│&emsp;├App.java<br>
&emsp;│&ensp;│&emsp;├Attendance.java<br>
&emsp;│&ensp;│&emsp;├AttendanceManager.java<br>
&emsp;│&ensp;│&emsp;├Matcher.java<br>
&emsp;│&ensp;│&emsp;├StampStatus.java<br>
&emsp;│&ensp;│&emsp;├TimeCard.java<br>
&emsp;│&ensp;│&emsp;└TimeStampDAO.java<br>
&emsp;│&ensp;└test/java/com/nyokinyoki/<br>
&emsp;│&ensp;&emsp;&emsp;└AppTest.java<br>
&emsp;├target/<br>
&emsp;├.gitignore<br>
&emsp;├index.sh<br>
&emsp;├Makefile<br>
&emsp;├NyokinyokiPirorin.db<br>
&emsp;├pom.xml<br>
&emsp;├README.md<br>
&emsp;└report.md<br>

## 各機能について

システム全体の動きの一例が以下である。

1. 履修登録を行う
1. ユーザが履修する講義を設定する
1. こちらで時間を設定して打刻を行う
1. 打刻済みの講義について、出席登録がされているかの確認をする

このシステムはユーザが1人であることを想定して作成した。

以降より各機能の説明を始める。

### 履修登録

「ニョキニョキピロリン」では、時間割の情報が格納されているDBから履修可能な科目を履修登録できる。
時間割を扱うクラスはCourseであり、Courseクラスのそれぞれのインスタンスは
- 講義ID
- 講義名
- TimeSlot(後述)

を情報として持っている。
TimeSlotクラスは、講義の曜日、開始時刻や終了時刻、さらに打刻の受付時間を定義している(この受付時間の間に打刻できているかどうかを出欠判定する)。
中には2コマにわたって開講されるものもあるため、それらに対応すべく開始時刻と終了時刻を分けた。
データベースの元となる講義は、私たちが3年前期で実際に履修できる講義であり、DBファイルであるNyokinyokiPirorin.dbファイルのテーブル「courses」に格納されている。


以上で講義の情報を一通り定義できた。次に、ユーザが実際に履修登録をし、履修登録された科目を保持する役割を果たすクラスについて説明する。

ユーザが履修登録をした科目は、TimeTableクラスに用意されたリストに格納される。
講義をリストに加えるメソッドはもちろんのこと、登録を解除するメソッド、履修可能な講義を全て表示するメソッドや、登録を希望する講義が存在しなかった場合にエラーを返すためのメソッドを作成した。
以上で履修登録の機能の説明を終える。


### 打刻・出欠判定

続いて、打刻を行う機能を説明する。実際にユーザが打刻を行った際に真っ先に呼び出されるのが、TimeCardクラスである。
TimeCardクラスのメソッドであるstamp()が呼び出されると、LocalDateTimeクラスを用いて現在時刻を取得し、TimeCardクラスが持つリストと、DBに打刻時刻が書き込まれる。

また、打刻した結果を用いて、きちんと打刻受付時間内に打刻ができているかを判定できる。
まずはAttendanceManagerクラスのメソッドにより打刻された時刻が打刻受付時間内のものであるかどうかを判定する。
そしてその結果をもとにAttendStatusクラスのインスタンスが作られ、これを出欠判定時に参照することで出席・欠席・遅刻・早退・未登録のいずれかの情報を講義ごとに持たせている。

## テスト

