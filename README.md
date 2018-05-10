# Gists Commenter

Comment in a given gist by its QR Code (its URL). You can create comments and edit and delete your own comments.

It's a simple app built in Kotlin with this use case in focus:
1. The user must open the Gist through a QRCode scan.
2. Only after the Gist’s opening should the user be able to comment on it.

### How to run
Add the [Github Personal Token](https://blog.github.com/2013-05-16-personal-api-tokens/) into `gradle.properties`

```properties
githubToken=<github personal token>
```

### [Gist](https://gist.github.com/bnsantos/1e708097a82e0f16c215) QR Code

<img src="qrcodes/qrcode.png" height="360">

### Used libs
* [Play Services Vision](https://developers.google.com/vision/android/barcodes-overview) (read QR Codes)
* [Retrofit](http://square.github.io/retrofit/) (network layer)
* [rxKotlin](https://github.com/ReactiveX/RxKotlin) (reactive extensions)
* [rxAndroid](https://github.com/ReactiveX/RxAndroid)
* [frecoLib](http://frescolib.org/) (Image loading)
* **TEST** [mockito](http://site.mockito.org/) (mock objects)
* **TEST** [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver) (A scriptable web server for testing HTTP clients)

### References
* [vision example](https://github.com/googlesamples/android-vision)
* [qr code generator](http://goqr.me/)
* [Github gists api](https://developer.github.com/v3/gists/)
