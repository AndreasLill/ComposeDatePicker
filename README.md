# Compose Date Picker
A simple light-weight and customizable `material3` date picker library for jetpack compose.

As of jetpack compose material3 version 1.1.0, a date picker does not yet exist.
This libary aims to add it until the official material date picker is released by google.

# Instructions
The library is published to the [jitpack.io](http://jitpack.io "jitpack.io") repository.
To use this library you must add jitpack to your project gradle repositories.

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```
Then add the library to dependencies.

```gradle
dependencies {
    implementation 'com.github.AndreasLill:ComposeDatePicker:1.0.3'
}
```

# Usage

Using the date picker is easy and can be further customized to suit your needs.

```kotlin
val dialogState = remember { mutableStateOf(false) }
val date = remember { mutableStateOf(LocalDate.now()) }

DatePickerDialog(
    state = dialogState,
    onSelectDate = {
        date.value = it
    }
)

Button(onClick = { dialogState.value = true }) {
    Text("Pick a date")
}
```
