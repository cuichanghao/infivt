# infivt
infinite Viewpager with Recycler Tablayout and cached Fragment

Reference:[RecyclerTabLayout](https://github.com/nshmura/RecyclerTabLayout),[InfiniteViewPager](https://github.com/antonyt/InfiniteViewPager)

## Demos
infinite demo:

```xml
<cc.cuichanghao.library.RecyclerTabLayout
        android:id="@+id/tab_layout"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        app:rtl_tabIndicatorColor="?attr/colorAccent"
        app:rtl_tabIndicatorHeight="0dp"
        app:rtl_tabTextAppearance="@style/TabTextStyleMain"
        app:rtl_tabSelectedTextColor="@android:color/black"
        app:rtl_tabMinWidth="72dp"
        app:rtl_tabMaxWidth="264dp"
        app:rtl_tabPaddingStart="22dp"
        app:rtl_tabPaddingEnd="22dp"
        app:rtl_selectType="oval"
        />
```

<img src="https://github.com/cuichanghao/infivt/blob/master/demo/ezgif.com-crop.gif" width="250" height="250">


screen fix partial rect demo:

```xml
    <cc.cuichanghao.library.RecyclerTabLayout
        android:id="@+id/tab_layout"
        android:layout_width="match_parent"
        android:layout_height="40dp"
        android:layout_marginStart="8dp"
        android:layout_marginEnd="8dp"
        app:rtl_tabIndicatorColor="?attr/colorAccent"
        app:rtl_tabIndicatorHeight="0dp"
        app:rtl_tabTextAppearance="@style/TabTextStyleSmall"
        app:rtl_tabSelectedTextColor="@android:color/black"
        app:rtl_tabOnScreenLimit="4"
        app:rtl_tabPaddingStart="8dp"
        app:rtl_tabPaddingTop="8dp"
        app:rtl_tabPaddingEnd="8dp"
        app:rtl_tabPaddingBottom="0dp"
        app:rtl_selectType="partialRect"
        />
```

<img src="https://github.com/cuichanghao/infivt/blob/master/demo/ezgif.com-crop_fix.gif" width="250" height="250">

## How to use
root build.gradle


```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

app build.gradle

```
implementation 'com.github.cuichanghao:infivt:<last commit Id>'
```


## License
```
Copyright (C) 2018 cuichanghao

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
