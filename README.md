# Fintara - Aplikasi Pinjaman Bank BCA

Selamat datang di **Fintara**, aplikasi pinjaman yang memungkinkan pengguna untuk mengajukan pinjaman ke **Bank BCA** dengan mudah. Aplikasi ini mengintegrasikan platform front-end responsif dengan back-end yang handal, serta aplikasi Android yang memudahkan pengguna dalam mengakses layanan pinjaman.

## Deskripsi

**Fintara** adalah platform aplikasi pinjaman yang dirancang untuk mempermudah proses pengajuan pinjaman bagi pengguna dan pengelolaan data pinjaman oleh manajemen Bank BCA. Pengguna dapat mengajukan pinjaman, melacak status pengajuan, dan mengelola interaksi keuangan mereka dengan Bank BCA. Sementara itu, manajemen bank dapat mengelola data pinjaman secara efektif menggunakan dashboard yang tersedia.

## Fitur Utama

### Fitur Pengguna
- **Pengajuan Pinjaman**: Pengguna dapat mengajukan pinjaman dengan mengisi data pribadi dan mengunggah dokumen yang diperlukan.
- **Pelacakan Status Pengajuan**: Pengguna dapat melacak status pengajuan pinjaman mereka secara real-time.
- **Antarmuka Pengguna yang Ramah**: Desain yang bersih, modern, dan responsif untuk pengalaman pengguna yang mulus di berbagai perangkat.

### Fitur Manajemen Bank
- **Manajemen Pengajuan**: Bank dapat memverifikasi, menyetujui, atau menolak pengajuan pinjaman.
- **Dashboard**: Dashboard untuk manajemen Bank BCA yang memudahkan pengelolaan pengajuan pinjaman dan analisis data.

## Teknologi yang Digunakan

### Back-End
- **Bahasa Pemrograman**: Java
- **Framework**: Spring Boot (RESTful API)
- **Desain Pattern**: MVC / Service Repository Pattern
- **Testing**: Unit Testing menggunakan JUnit
- **Deployment**: Google Cloud Platform (GCP), CI/CD menggunakan Github Actions
- **Dokumentasi**: API yang dapat diakses secara publik

### Front-End
- **Bahasa Pemrograman**: TypeScript, HTML, CSS
- **Framework**: Angular
- **Desain**: Desain responsif (Mobile-first)
- **Testing**: Unit Testing menggunakan Jasmine, Integration Testing menggunakan Protractor
- **Deployment**: Vercel, CI/CD menggunakan Github Actions

### Aplikasi Android
- **Bahasa Pemrograman**: Kotlin
- **Framework**: Android Studio
- **Fitur**: Aplikasi mobile untuk pengguna mengajukan pinjaman dan melacak status pinjaman
- **Deployment**: Play Store (Opsional)

## Cara Menjalankan Secara Lokal

### Prasyarat
1. Pastikan sudah menginstal [Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) dan [Maven](https://maven.apache.org/download.cgi) untuk back-end.
2. Instal [Node.js](https://nodejs.org/) dan [Angular CLI](https://angular.io/cli) untuk front-end.
3. Instal [Android Studio](https://developer.android.com/studio) untuk aplikasi Android.

### Back-End (Spring Boot)
1. Clone repositori:
   ```bash
   git clone https://github.com/username/Fintara.git
