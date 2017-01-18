# Belajar Spring Cloud #

Studi Kasus :

Aplikasi online shop yang dibuat dengan microservice. Daftar semua modul terlihat seperti ini:

![Arsitektur Aplikasi](catatan/img/arsitektur-olshop.jpg)

Keterangan:

* hitam : user/client apps
* merah : service yang bisa diakses dari user
* biru : service infrastruktur, untuk mengkoordinasikan service lain
* hijau : service internal, tidak bisa diakses langsung dari luar

## Komponen Aplikasi ##

Aplikasi lengkap akan terdiri dari beberapa microservices, antara lain:

* discovery-service : microservice yang bertugas untuk menyimpan daftar semua service. Terdiri dari 2 instance untuk keperluan failover
* folder config : repository config yang akan dibaca oleh Config Server
* config-server : microservice yang menghosting semua konfigurasi service lain
* catalog-service : microservice untuk menyediakan REST API daftar produk dan informasi produk
* image-service : microservice untuk hosting image. Nantinya ini akan digantikan oleh CDN yang betulan.
* online-store : aplikasi web yang menggunakan `catalog-service` untuk menampilkan daftar produk dalam format HTML/website dan mengambil foto dari image service
* api-gateway : microservice untuk menjadi pintu masuk semua request, kemudian mem-forward request ke service yang berwenang
* auth-service : microservice untuk login

Urutan development:

1. Implement discovery service
2. Implement config server
3. Implement catalog service
4. Implement image service
4. Implement online store
5. Implement security
6. Implement shopping cart
7. Implement merchant service
8. Implement merchant webapp

Urutan deployment:

1. Discovery service : kita akan mendeploy 2 instance agar bisa failover. Inilah satu-satunya service yang harus diketahui lokasi fisiknya (hostname dan port) oleh service lain. Untuk service lainnya, lokasi fisik bisa berpindah-pindah. Service satu menghubungi service lain dengan menggunakan namanya. Lokasi fisiknya akan disediakan oleh `discovery-service`.

2. Config repo dan config server. Setelah dideploy, dia akan mendaftarkan diri ke discovery services agar bisa diakses oleh service lain.
3. Backend Services : yaitu microservices yang bertugas menyediakan fungsi-fungsi aplikasi (misalnya catalog, shopping cart, dsb)
4. API Gateway : pintu masuk untuk seluruh backend services
5. Authentication Service

Kecuali `discovery-service`, semua service lain bisa dijalankan berbarengan berkali-kali untuk mendapatkan clustering/multiple instance. Misalnya, bila kita jalankan `catalog-service` di dua console secara berbarengan, kita akan mendapatkan 2 instance `catalog-service` yang bisa melayani request secara bergantian. Ini disebut dengan replication atau clustering.

Khusus `discovery-service`, replikasinya tidak otomatis, karena sesama `discovery-service` harus tau persis lokasi (host dan port) temannya.

## Discovery Service ##

Ini adalah aplikasi yang menyimpan database berisi semua nama aplikasi dan lokasinya (host dan port). Ada beberapa implementasi discovery services, antara lain:

* Eureka
* Consul
* etcd
* Zookeeper

Tapi pada contoh ini, kita gunakan Eureka yang supportnya paling lengkap di Spring Boot. Lagipula, Consul dan Zookeeper membutuhkan instalasi aplikasi secara standalone, sehingga ribet setupnya. Eureka bisa dijalankan embedded dalam Spring Boot.

Eureka ingin dijalankan dengan lebih dari satu instance. Tujuannya supaya kalau satu node mati, masih ada cadangan. Bila kita tetap nekat menjalankan satu instance, bisa jalan tapi lognya berisik. Setiap sekian detik dia menampilkan pesan error karena tidak ketemu node lain.

Untuk itu, kita buat dua profile supaya satu aplikasi ini bisa dijalankan menjadi dua instance, yang satu jalan di port `10001`, satu lagi jalan di port `10002`.

Berikut cara menjalankan instance pertama:

        SPRING_PROFILES_ACTIVE=discovery1 mvn spring-boot:run

Dan berikut perintah untuk menjalankan instance kedua:

        SPRING_PROFILES_ACTIVE=discovery2 mvn spring-boot:run

Setelah keduanya jalan, kita bisa lihat service mana saja yang terdaftar dengan mengakses ke `http://localhost:10001/`

### Deployment ke Heroku ###

Berikut adalah rangkaian perintah untuk mendeploy aplikasi `discovery-service` ke Heroku. Semua perintah dijalankan di command line dalam folder hasil clone repo ini.

```
heroku apps:create --remote heroku-discovery1 --region us belajar-springcloud-discovery1
heroku config:set SPRING_PROFILES_ACTIVE=heroku-discovery1  --remote heroku-discovery1
git subtree push --prefix discovery-service heroku-discovery1 master
```

Bila terjadi kesalahan dan ingin redeploy, cara termudah adalah dengan cara menghapus dulu aplikasinya di Heroku dengan perintah berikut

```
heroku apps:destroy --remote heroku-discovery1 --confirm belajar-springcloud-discovery1
```

Khusus untuk `discovery-service`, kita perlu mendeploy replikasinya. Berikut perintahnya

```
heroku apps:create --remote heroku-discovery2 --region eu belajar-springcloud-discovery2
heroku config:set SPRING_PROFILES_ACTIVE=heroku-discovery2  --remote heroku-discovery2
git subtree push --prefix discovery-service heroku-discovery2 master
```

Bila terjadi kesalahan dan ingin redeploy hasil replikasi ini, cara termudah adalah dengan cara menghapus dulu aplikasinya di Heroku dengan perintah berikut

```
heroku apps:destroy --remote heroku-discovery2 --confirm belajar-springcloud-discovery2
```

## Config Server ##

Cara menjalankan:

1. Masuk ke foldernya

        cd config-server

2. Jalankan

        mvn clean spring-boot:run

3. Coba akses konfigurasi aplikasi `catalog` dengan profile `testing`

        curl http://localhost:8888/catalog/testing

4. Periksa outputnya, seharusnya seperti ini

        {
          "name" : "catalog",
          "profiles" : [ "testing" ],
          "label" : "master",
          "version" : "4bf57b8c943d17ff8b4664a8a70f93080c134a36",
          "state" : null,
          "propertySources" : [ {
            "name" : "https://github.com/endymuhardin/belajar-springcloud.git/config/catalog/application-testing.yml",
            "source" : {
              "nama" : "Test User"
            }
          }, {
            "name" : "https://github.com/endymuhardin/belajar-springcloud.git/config/catalog/application.yml",
            "source" : {
              "server.port" : 30001,
              "spring.datasource.url" : "jdbc:mysql://localhost/belajar",
              "spring.datasource.username" : "belajar",
              "nama" : "Development User",
              "spring.datasource.password" : "passwordDBdevelopment"
            }
          }, {
            "name" : "https://github.com/endymuhardin/belajar-springcloud.git/config/application.yml",
            "source" : {
              "spring.jackson.serialization.indent_output" : true
            }
          } ]
        }

5. Bila `spring.datasource.password` tidak terdekripsi, jalankan `config-server` dengan environment variable `ENCRYPT_KEY`

        ENCRYPT_KEY=inisymmetrickeyrahasiasekali mvn clean spring-boot:run

6. Bila mendapatkan output seperti dibawah ini, pastikan kita sudah menginstal Java Cryptography Extension seperti dijelaskan [di sini](http://stackoverflow.com/a/6481658)

        {
          "timestamp" : 1483025777244,
          "status" : 500,
          "error" : "Internal Server Error",
          "exception" : "java.lang.IllegalArgumentException",
          "message" : "Unable to initialize due to invalid secret key",
          "path" : "/encrypt"
        }

7. Sedangkan bila mendapatkan error seperti ini, berarti kita tidak memasang symmetric key dengan benar.

        {
          "description" : "No key was installed for encryption service",
          "status" : "NO_KEY"
        }

8. Selanjutnya, `config-server` ini seharusnya kita deploy ke cloud service seperti Heroku supaya mendapatkan hostname yang permanen. Soalnya nanti semua aplikasi lain akan mengambil konfigurasi dari `config-server` ini.

9. Bila ingin mengenkrip suatu nilai, misalnya `1234`, kita bisa menggunakan endpoint `/encrypt` seperti ini

        curl http://localhost:8888/encrypt -d 1234

   dan mendapatkan hasil seperti ini

        59e78e6f57c36a0e2347cf1f68ae7772594e1f77b3cb2bb358baf447cd304eda

10. Nilai tersebut bisa kita pasang di file konfigurasi seperti ini

        password: {cipher}59e78e6f57c36a0e2347cf1f68ae7772594e1f77b3cb2bb358baf447cd304eda

### Deployment ke Heroku ###

Berikut adalah rangkaian perintah untuk mendeploy aplikasi `config-server` ke Heroku. Semua perintah dijalankan di command line dalam folder hasil clone repo ini.

```
heroku apps:create --remote heroku-configsvr --region us belajar-springcloud-configsvr
heroku config:set ENCRYPT_KEY=inisymmetrickeyrahasiasekali --remote heroku-configsvr
git subtree push --prefix config-server heroku-configsvr master
```

Bila terjadi kesalahan dan ingin redeploy, cara termudah adalah dengan cara menghapus dulu aplikasinya di Heroku dengan perintah berikut

```
heroku apps:destroy --remote heroku-configsvr --confirm belajar-springcloud-configsvr
```

## Catalog Service ##

Berisi REST API untuk mendapatkan daftar produk dan URL fotonya.


Pada saat dijalankan, aplikasi ini:

1. Menghubungi `discovery service` untuk mendaftarkan diri dan mencari `config server`
2. Menghubungi `config server` untuk mendapatkan konfigurasi
3. Barulah menjalankan diri sendiri

Untuk menjalankan aplikasi ini, tidak ada yang istimewa, langsung saja

        mvn clean spring-boot:run


Perhatikan log filenya untuk mendapatkan port dimana dia berjalan (karena portnya diset random). Cari baris seperti ini di log

```
Updating port to 58255
```

Kemudian browse ke `http://localhost:58255/halo`. Kita akan mendapatkan output sebagai berikut:

```js
{
  "nama" : "Development User",
  "waktu" : "Fri Dec 30 00:41:00 WIB 2016"
}
```

Untuk mengetes apakah konfigurasi berjalan sempurna, kita bisa coba jalankan profile `testing` dengan cara mengeset environment variable `SPRING_PROFILES_ACTIVE` seperti ini

        SPRING_PROFILES_ACTIVE=testing mvn clean spring-boot:run

Harusnya `catalog service` akan menjalankan konfigurasi profile `testing` sehingga variabel `nama` akan berisi `Test User` seperti ini

```js
{
  "nama" : "Test User",
  "waktu" : "Fri Dec 30 00:43:47 WIB 2016"
}
```

Beberapa endpoint lain yang tersedia:

* daftar produk : `http://localhost:58255/product`
* foto produk : `http://localhost:58255/product/p001/photos`

Ganti `58255` dengan nilai port yang didapatkan dari logfile. Ganti `p001` dengan id produk yang tersedia.

### Deployment ke Heroku ###

Berikut adalah rangkaian perintah untuk mendeploy aplikasi `catalog-service` ke Heroku. Semua perintah dijalankan di command line dalam folder hasil clone repo ini.

```
heroku apps:create --remote heroku-catalog --region us belajar-springcloud-catalog
git subtree push --prefix catalog-service heroku-catalog master
```

Bila terjadi kesalahan dan ingin redeploy, cara termudah adalah dengan cara menghapus dulu aplikasinya di Heroku dengan perintah berikut

```
heroku apps:destroy --remote heroku-catalog --confirm belajar-springcloud-catalog
```

## Image Service ##

Cara menjalankan:

1. Masuk ke foldernya

        cd image-service

2. Jalankan

        mvn clean spring-boot:run

3. Beberapa endpoint yang tersedia

    * /p001/img001.jpg
    * /p001/img002.jpg
    * /p001/img003.jpg
    * /p002/img001.jpg
    * /p002/img002.jpg
    * /p002/img003.jpg
    * /p003/img001.jpg
    * /p003/img002.jpg
    * /p003/img003.jpg

### Deployment ke Heroku ###

Berikut adalah rangkaian perintah untuk mendeploy aplikasi `image-service` ke Heroku. Semua perintah dijalankan di command line dalam folder hasil clone repo ini.

```
heroku apps:create --remote heroku-image --region us belajar-springcloud-image
git subtree push --prefix image-service heroku-image master
```

Bila terjadi kesalahan dan ingin redeploy, cara termudah adalah dengan cara menghapus dulu aplikasinya di Heroku dengan perintah berikut

```
heroku apps:destroy --remote heroku-image --confirm belajar-springcloud-image
```


## Online Store ##

Cara menjalankan:

1. Masuk ke foldernya

        cd online-store

2. Jalankan

        mvn clean spring-boot:run

3. Beberapa endpoint yang tersedia

    * /product/list : daftar semua produk
    * /p001m001 : detail produk berdasarkan permalink
    * /p002m001
    * /p003m001

Jangan heran bila foto-foto produk tidak tampil. Ini disebabkan karena linknya tidak jalan di aplikasi ini. Link foto disediakan oleh `image-service`. Agar link foto berfungsi, website online store harus diakses melalui `api-gateway`

### Deployment ke Heroku ###

Berikut adalah rangkaian perintah untuk mendeploy aplikasi `online-store` ke Heroku. Semua perintah dijalankan di command line dalam folder hasil clone repo ini.

```
heroku apps:create --remote heroku-olstore --region us belajar-springcloud-olstore
git subtree push --prefix online-store heroku-olstore master
```

Bila terjadi kesalahan dan ingin redeploy, cara termudah adalah dengan cara menghapus dulu aplikasinya di Heroku dengan perintah berikut

```
heroku apps:destroy --remote heroku-olstore --confirm belajar-springcloud-olstore
```

## API Gateway ##

Cara menjalankan:

1. Masuk ke foldernya

        cd api-gateway

2. Jalankan

        mvn clean spring-boot:run

3. Beberapa endpoint yang tersedia (sama dengan endpoint online-store)

    * /product/list : daftar semua produk
    * /p001m001 : detail produk berdasarkan permalink
    * /p002m001
    * /p003m001

Bedanya dengan akses langsung ke `online-store`, pada aplikasi ini semua link image akan berjalan dengan baik. Ini bisa dilihat dengan tampilnya foto produk.

### Deployment ke Heroku ###

Berikut adalah rangkaian perintah untuk mendeploy aplikasi `api-gateway` ke Heroku. Semua perintah dijalankan di command line dalam folder hasil clone repo ini.

```
heroku apps:create --remote heroku-apigw --region us belajar-springcloud-apigw
git subtree push --prefix api-gateway heroku-apigw master
```

Bila terjadi kesalahan dan ingin redeploy, cara termudah adalah dengan cara menghapus dulu aplikasinya di Heroku dengan perintah berikut

```
heroku apps:destroy --remote heroku-apigw --confirm belajar-springcloud-apigw
```

## Auth Service ##

Security pada aplikasi ini akan menggunakan protokol OAuth 2.0 dengan format token JWT. Untuk itu, kita perlu membuat keystore dulu untuk menyimpan private key dan public key. Berikut perintah untuk membuatnya

```
keytool -genkeypair -alias jwt -keyalg RSA -dname "CN=Endy Muhardin, OU=Belajar Microservices, O=ArtiVisi, L=Jakarta, ST=Jakarta, C=ID" -keypass rahasia -keystore src/main/resources/jwt.jks -storepass rahasia
```

Kemudian, kita perlu mengeluarkan public key dari keystore tersebut untuk dipasang di semua service lainnya. Berikut perintahnya

```
keytool -list -rfc --keystore src/main/resources/jwt.jks | openssl x509 -inform pem -pubkey
```

Contoh outputnya seperti ini

```
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuZzI5JJClLD69mLAQRlg
cVT6pDL1SoiAngZW8wDcELPDXTSYfhy00N7H+b6X+qpZRMzaLikzcp/qDRtCMIlB
QzyWKq3nPlSu98MEhEvCb7Q8S/4OQLhR0RYn4oPsvMx6KBh+UdJBukBqi6U5/Ew1
H93zd7S6VfzmlLFzLTnbSUfKeB9g2IdVqU1x2Sehxx93TnMcC/RpODy0e0cbetzV
/PG2zI2JHUTZylb5PMNZQm5cfFtUIb/bgH9nAXYxhLXKqbyplDGh/uIgayZfrStR
CI2sB/w0HCqf6VSf0JBR4m1r7Mr4Pr7SZcyIjYz36CULYw89yht2N5NicqckYdYO
gQIDAQAB
-----END PUBLIC KEY-----
-----BEGIN CERTIFICATE-----
MIIDlzCCAn+gAwIBAgIEA3MB8jANBgkqhkiG9w0BAQsFADB8MQswCQYDVQQGEwJJ
RDEQMA4GA1UECBMHSmFrYXJ0YTEQMA4GA1UEBxMHSmFrYXJ0YTERMA8GA1UEChMI
QXJ0aVZpc2kxHjAcBgNVBAsTFUJlbGFqYXIgTWljcm9zZXJ2aWNlczEWMBQGA1UE
AxMNRW5keSBNdWhhcmRpbjAeFw0xNzAxMDQwNDI3NTRaFw0xNzA0MDQwNDI3NTRa
MHwxCzAJBgNVBAYTAklEMRAwDgYDVQQIEwdKYWthcnRhMRAwDgYDVQQHEwdKYWth
cnRhMREwDwYDVQQKEwhBcnRpVmlzaTEeMBwGA1UECxMVQmVsYWphciBNaWNyb3Nl
cnZpY2VzMRYwFAYDVQQDEw1FbmR5IE11aGFyZGluMIIBIjANBgkqhkiG9w0BAQEF
AAOCAQ8AMIIBCgKCAQEAuZzI5JJClLD69mLAQRlgcVT6pDL1SoiAngZW8wDcELPD
XTSYfhy00N7H+b6X+qpZRMzaLikzcp/qDRtCMIlBQzyWKq3nPlSu98MEhEvCb7Q8
S/4OQLhR0RYn4oPsvMx6KBh+UdJBukBqi6U5/Ew1H93zd7S6VfzmlLFzLTnbSUfK
eB9g2IdVqU1x2Sehxx93TnMcC/RpODy0e0cbetzV/PG2zI2JHUTZylb5PMNZQm5c
fFtUIb/bgH9nAXYxhLXKqbyplDGh/uIgayZfrStRCI2sB/w0HCqf6VSf0JBR4m1r
7Mr4Pr7SZcyIjYz36CULYw89yht2N5NicqckYdYOgQIDAQABoyEwHzAdBgNVHQ4E
FgQUrM7LKWO3aPV6Av+eEbWuUdsJrk0wDQYJKoZIhvcNAQELBQADggEBABGa4B9W
lVoRRNfu1hMW2/2N30zCJuBP2upLy1ukZwIsnWt0brNI9WqB1JStuEJSpKSkCDLZ
uwomG8QDGr4ur9qER4WMst4ZXx1XYjQ/Yhm5lCHcLr/cLTQs+LHbEZ4G2UFq5/th
wr55EEfMcmwfNA7Rt7IRRUDjxrDGjHd8v+HwZzXwjkbeGzQ9UB26OMIjeGOSazT0
hLZXNI8kXFJr7XIgOFXiQA2EcUj2g3tSvYw6K0WOZe4vv2iQq+oRx4dDmJJQsP3X
cIZ5RkDMp0II1NblONrcl48PCiv/0n2wjkRLTtz0zKMsAXNVRh+gvcNr0QyDkLvP
HMl7Vjn2hTXCFcI=
-----END CERTIFICATE-----
```

Kita cukup ambil bagian public keynya saja, yaitu

```
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuZzI5JJClLD69mLAQRlg
cVT6pDL1SoiAngZW8wDcELPDXTSYfhy00N7H+b6X+qpZRMzaLikzcp/qDRtCMIlB
QzyWKq3nPlSu98MEhEvCb7Q8S/4OQLhR0RYn4oPsvMx6KBh+UdJBukBqi6U5/Ew1
H93zd7S6VfzmlLFzLTnbSUfKeB9g2IdVqU1x2Sehxx93TnMcC/RpODy0e0cbetzV
/PG2zI2JHUTZylb5PMNZQm5cfFtUIb/bgH9nAXYxhLXKqbyplDGh/uIgayZfrStR
CI2sB/w0HCqf6VSf0JBR4m1r7Mr4Pr7SZcyIjYz36CULYw89yht2N5NicqckYdYO
gQIDAQAB
-----END PUBLIC KEY-----
```

Aplikasi `auth-server` ini dijalankan seperti biasa, menggunakan perintah

```
mvn clean spring-boot:run
```

Untuk mengetes link login, browse ke `http://localhost:50237/oauth/authorize?client_id=onlinestore&response_type=code&redirect_uri=http://example.com`

Pastikan ganti portnya sesuai yang muncul di log. Kita akan mendapatkan halaman login. Isikan username/password sesuai yang sudah dihardcode di class `KonfigurasiSecurity`.

Selanjutnya, kita akan diredirect ke `http://example.com/?code=iT30S8`. Ambil code dan tukarkan dengan access_token dengan perintah berikut:

```
curl -X POST -vu onlinestore:123456 http://localhost:50237/oauth/token -H "Accept: application/json" -d "grant_type=authorization_code&code=iT30S8&redirect_uri=http://example.com"
```

Seharusnya kita akan mendapatkan access token sebagai berikut:

```js
{
  "access_token" : "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiYmVsYWphcnNwcmluZ2Nsb3VkIl0sInVzZXJfbmFtZSI6ImVuZHkiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNDgzNTQ4MjgwLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6IjdhMGNlNzVjLWMzZWItNDFjMy04NzY0LWQ2MmMwMWRiMDQ4MyIsImNsaWVudF9pZCI6Im9ubGluZXN0b3JlIn0.ds1fVItcI9xJY4jM2uFQ_vf9KnhMayj-vZ7peJ0ZcedicylCi2zExblThQ788-2ZC0boCpeTppWS5j4tPsqoR_TRduTjL-r0VC5tlWK9YOarlr4Kyu0xfI9_6U5mLl1RoHs_VnIBJIOliOjOKbj2CNBOj2UMgDzPNuPxrblCqJ28_J9OrBdyHC0Z9sfOSeM7HIyx44-pSNZi4U9jbrnStpnB_iBCafmjbRUUaZ4_YpNp-uAPJKPd-bAMOPtJb2APlSWmbyj1Ay881cU9PS4KoPOMm-dzcPef2uu6tLzWGKkMse9rFPOwslUtuBuxJ8QPE3z0M8d2MQiI9skJr1FtiQ",
  "token_type" : "bearer",
  "refresh_token" : "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiYmVsYWphcnNwcmluZ2Nsb3VkIl0sInVzZXJfbmFtZSI6ImVuZHkiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiYXRpIjoiN2EwY2U3NWMtYzNlYi00MWMzLTg3NjQtZDYyYzAxZGIwNDgzIiwiZXhwIjoxNDg2MDk3MDgwLCJhdXRob3JpdGllcyI6WyJST0xFX0FETUlOIl0sImp0aSI6IjBmZTlmNWNkLWQxODktNDQwYi05NTI5LTdlOThjYjk3Nzg1YyIsImNsaWVudF9pZCI6Im9ubGluZXN0b3JlIn0.MBCvkWQP9imbs0Q2W_bDgAFa1CbWvXD0Zb2XmycFXPRKF19qEvkTj8wIjpCtWnpVa2YruP5gLfv_kIILUJFycf1WcHhhRy3pErA8x8lcUN1a_ty3LSrudPdyy7i1U2oGKzHO3cadDYWAQt2CesbLleXGKbZNeUJHS30IeKH0sZumMTKKv_YuTPaol3qwi3rmHfs4HRH1ANPpv5h8_EFzMVfwjnUFxm_pKNSt3YZj2jpa3u8eJWASePYGpIxcnfvxgOC-6V9-FNJGNSB70Eq6qD8YOFsVN0FSVE1Z_JdXMOC4JM37HNn6inecaAe0fDO81Y8HjgCcthhyMHngWWEBuw",
  "expires_in" : 43199,
  "scope" : "read write",
  "jti" : "7a0ce75c-c3eb-41c3-8764-d62c01db0483"
}
```

Kita bisa copy-paste nilai `access_token` pada response di atas ke website [https://jwt.io](https://jwt.io/) untuk mengetahui isinya.

![Decoding JWT Token](catatan/img/jwt-decoded.png)

### Catatan tentang cookie ###

Bila kita jalankan `auth-server` ini dengan aplikasi lain yang menggunakannya untuk security (misal sebagai SSO Client atau Resource Server) di host yang sama, perlu ada perhatian khusus tentang cookie.

Aplikasi `auth-server` dan aplikasi lain menggunakan cookie sebagai penanda session, biasanya bernama `JSESSIONID` seperti layaknya aplikasi web berbasis Java pada umumnya. Cookie ini diset menggunakan hostname dan contextPath, tidak menggunakan port.

[![Cookie JSESSIONID](catatan/img/nama-cookie-default.png)](catatan/img/nama-cookie-default.png)

Dengan demikian, apabila kedua aplikasi dijalankan di host yang sama (misalnya di `localhost`), maka keduanya akan berusaha memasang cookie bernama `JSESSIONID` dengan contextPath `/` seperti bisa dilihat pada screenshot berikut. Pertama, kita lihat cookie aplikasi `auth-server` yang berjalan di port 10003.

[![Cookie Auth Server](catatan/img/cookie-auth-server.png)](catatan/img/cookie-auth-server.png)

Begitu kita selesai login, akan diredirect ke aplikasi `api-gateway` di port 10000. Aplikasi `api-gateway` terlihat tidak bisa memproses `authorization_code` yang didapatkan untuk melakukan single-sign-on. Hasilnya akan tampak seperti ini.

[![Cookie API Gateway](catatan/img/cookie-api-gateway.png)](catatan/img/cookie-api-gateway.png)

Kalau kita perhatikan di tab cookie, kita akan lihat bahwa cookie yang diset sama persis konfigurasinya seperti milik `auth-server` tadi. Bedanya cuma isinya saja. Bila kita ulangi proses login beberapa kali, kita akan melihat nilainya berubah-ubah terus. Ini disebabkan karena aplikasi berusaha mencari `JSESSIONID` yang ada dalam cookie, tidak berhasil menemukan, kemudian memasang `JSESSIONID` baru.

Agar aplikasi `auth-server` dan `api-gateway` tidak saling menimpa cookie, ada 3 alternatif yaitu:

* mengganti `contextPath` salah satu aplikasi dengan konfigurasi seperti ini

    ```yaml
    server:
        contextPath:/auth
    ```

* mengganti hostname salah satu aplikasi, misalnya dari `http://localhost:10003` menjadi `http://authserver:10003`. Perubahan ini dilakukan dengan cara menambahkan konfigurasi di `/etc/hosts` seperti ini

    ```
    127.0.0.1       authserver
    ```

* mengganti nama cookie, jangan pakai nama default `JSESSIONID`. Konfigurasinya seperti ini

    ```yaml
    server:
        session:
            cookie:
                name:AUTHSERVER
    ```

Dari ketiga alternatif ini, biasanya saya menggunakan alternatif ganti nama cookie, supaya tidak mengubah konfigurasi nama host ataupun `contextPath`. Hasilnya seperti ini.

[![Ganti Nama Cookie](catatan/img/ganti-nama-cookie.png)](catatan/img/ganti-nama-cookie.png)

Kita bisa lihat bahwa nama cookie berbeda sehingga masing-masing aplikasi bisa menggunakan `session id` masing-masing.

## Referensi ##

The Big Picture

* http://www.kennybastani.com/2016/04/event-sourcing-microservices-spring-cloud.html
* https://www.youtube.com/watch?v=cCEvFDhe3os
* https://www.youtube.com/watch?v=5q8B6lYhFvE
* https://programmaticponderings.com/2016/02/15/diving-deeper-into-getting-started-with-spring-cloud/

Discovery Service

* http://www.baeldung.com/spring-cloud-netflix-eureka
* http://www.todaysoftmag.com/article/1429/micro-service-discovery-using-netflix-eureka
* https://blog.heroku.com/managing_your_microservices_on_heroku_with_netflix_s_eureka

OAuth

* https://spring.io/guides/tutorials/spring-security-and-angular-js/
* http://stytex.de/blog/2016/02/01/spring-cloud-security-with-oauth2/
* https://github.com/spring-guides/tut-spring-security-and-angular-js/tree/master/oauth2
