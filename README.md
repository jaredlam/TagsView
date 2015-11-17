# TagsView

- [Introduction](#introduction)
- [Download](#download)
- [Usage](#usage)
- [To-Do List](#to-do-list)
- [License](#license)

# Introduction

TagsView is a Android Library that can contains its children without cut them off, and support multiple lines.

![alt tag](https://raw.githubusercontent.com/jaredlam/TagsView/master/TagsViewDemo/image/screenshot.png)

# Download
```groove
dependencies {
    compile 'com.jaredlam.tagsview:TagsViewLibrary:0.1.1'
}
```
# Usage

```xml
<com.jaredlam.tagsview.TagsView
   android:id="@+id/tags_view"
   android:layout_width="300dp"
   android:layout_height="wrap_content"
   android:layout_marginTop="10dp"
   android:background="@color/gray_light"
   android:padding="10dp" />
```

```java
TagsView tagsView = (TagsView) findViewById(R.id.tags_view);
tagsView.setWillShiftAndFillGap(true);

for (int i = 0; i < labels.length; i++) {
    TextView tag = new TextView(this);
    tag.setTextColor(getResources().getColor(android.R.color.black));
    tag.setBackgroundResource(colors[i]);
    tag.setText(labels[i]);
    tag.setPadding(10, 10, 10, 10);
    tagsView.addView(tag);
}
```

# To-Do List

- Add support for multiple lines

# License

Copyright (C) 2015 Jared Luo
jaredlam86@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

