## internalスコープとtestソースの関係


### 雑なbuildTypeとproductFlavors

Android Gradle Pluginでは *buildType* と *productFlavors* を使用することで特定の用途のビルド設定を記述することができます。

例:

* buildType ... debugビルド, releaseビルド
* productFlavors ... devフレーバー、 productionフレーバー

そして、 buildType × productFlavorsの組み合わせが *buildVariants* として選択できるようになります。

例:

* buildVariants ... devDebg, releaseProductionなど

単体テストを実行する際には、何かしろの *buildVariants* を指定してます。

### 問題

Javaではテスト用にアクセス修飾子をパッケージプライベートにして、テストからアクセスできるようにすることがあります。
Kotlinではパッケージプライベートが使えないため、internalで代用していました。

例えば、mainソース以下にinternalスコープのクラスもしくはプロパティが定義されているとします。

* `src/main/com/github/droibit/kotlin/internal/test/InternalClass.kt`

```InternalClass.kt
internal class InternalClass {
    val publicValue = 1
    internal val internalValue = 2
}
```

ユーザ定義の *buildVariants* を選択して単体テストを実行すると、

* `src/test/com/github/droibit/kotlin/internal/test/InternalClassTest.kt`

```InternalClassTest.kt
class InternalClassTest {

    @Test
    fun test() {
        val internalClass = InternalClass()

        assert(internalClass.publicValue == 1)
        assert(internalClass.internalValue == 2)
    }
}
```

`Cannot access 'InternalClass': it is 'internal' in 'test'`
`Cannot access 'internalValue': it is 'internal' in 'InternalClass'`

ビルド時に以上のようなエラーが出ます。
そのため、internalなアクセス修飾子がついているとテストが実行できないことがあります。

#### テストパターン

成功・失敗パターンは次のとおりです。

*  app-buildtypesモジュール ... *buildVariants*

|buildVariants| ビルドの成否|
|----|----|
|debug|○|
|debugMock|×|
|release|○|

* app-flavorモジュール ... *productFlavors*

|buildVariants| ビルドの成否|
|----|----|
|devDebug|×|
|devRelease|×|
|productionDebug|×|
|productionRelease|×|
