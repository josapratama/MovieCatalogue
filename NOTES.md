# Movie Catalogue - Catatan Submission

## Fitur Utama

1. **List Movie (Home)** — Menampilkan daftar film populer dari TheMovieDB API dengan shimmer loading effect
2. **Detail Movie** — Detail film lengkap: backdrop, poster, rating, jumlah vote, tanggal rilis, bahasa, overview, dan tombol favorite
3. **Favorite Movie** — Menyimpan dan menampilkan film favorit menggunakan Room Database (Dynamic Feature Module)

---

## Kriteria Wajib

### ✅ 1. Continuous Integration (GitHub Actions)

**File:** `.github/workflows/android.yml`

Pipeline terdiri dari 3 job:

- **test** — Menjalankan `./gradlew :core:testDebugUnitTest` (47 unit test)
- **build** (depends on `test`) — Membangun debug APK dengan `./gradlew assembleDebug`, mengupload artefak APK
- **lint** — Menjalankan `./gradlew :app:lintDebug :core:lintDebug`, mengupload laporan HTML

CI berjalan otomatis pada setiap `push` dan `pull_request` ke branch `main`/`master`.

---

### ✅ 2. Performa — LeakCanary

**File:** `app/build.gradle`

```groovy
debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.12'
```

LeakCanary diinisialisasi otomatis melalui `ContentProvider` pada build debug — tidak perlu kode tambahan di `Application`. Notifikasi memory leak muncul langsung di notification bar perangkat selama pengujian.

---

### ✅ 3. Security

#### a. Obfuscation dengan ProGuard / R8

**Lokasi:** `app/proguard-rules.pro`, `core/proguard-rules.pro`, `core/consumer-rules.pro`

- `minifyEnabled true` aktif di `buildTypes.release` pada modul `app` dan `core`
- `app/proguard-rules.pro`: Hanya mem-preserve kelas Presentation Layer dan DI, tidak lagi memakai `-keep class com.dicoding.moviecatalogue.** { *; }` yang terlalu luas
- Rules lengkap untuk semua library: Retrofit, OkHttp, Gson, Room, SQLCipher, Koin, Coil, Kotlin Coroutines, AndroidX

#### b. Enkripsi Database dengan SQLCipher

**Lokasi class:** `core/src/main/java/com/dicoding/moviecatalogue/core/data/source/local/room/MovieDatabase.kt`

```kotlin
val passphrase: ByteArray = SQLiteDatabase.getBytes("mc_s3cur3_p@ssphr4s3".toCharArray())
val factory: SupportSQLiteOpenHelper.Factory = SupportFactory(passphrase)

Room.databaseBuilder(context, MovieDatabase::class.java, "Movie.db")
    .openHelperFactory(factory)
    .build()
```

**Dependencies yang ditambahkan di `core/build.gradle`:**

```groovy
implementation 'net.zetetic:android-database-sqlcipher:4.5.4'
implementation 'androidx.sqlite:sqlite-ktx:2.4.0'
```

File database `Movie.db` dienkripsi sepenuhnya menggunakan AES-256. Tanpa passphrase yang benar, database tidak dapat dibuka oleh tools eksternal.

#### c. Certificate Pinning

**Lokasi class:** `core/src/main/java/com/dicoding/moviecatalogue/core/data/source/remote/network/ApiConfig.kt`

```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("api.themoviedb.org", "sha256/QfyoR20v8hyYX7L+ikLzM/euPGSDl67gFFcor/sROMs=") // leaf
    .add("api.themoviedb.org", "sha256/G9LNNAql897egYsabashkzUCTEJkWBzgoEtk8X/678c=") // intermediate
    .build()
```

- **Pin 1 (leaf):** SHA-256 SPKI dari sertifikat `*.themoviedb.org` (issuer: Amazon RSA 2048 M04, berlaku hingga Desember 2026)
- **Pin 2 (intermediate / backup):** SHA-256 SPKI dari Amazon RSA 2048 M04 — sebagai backup agar aplikasi tetap berfungsi saat TMDB merotasi sertifikat leaf mereka
- Pin diperoleh langsung dari live server menggunakan `openssl s_client` + `openssl dgst -sha256`
- Mencegah serangan MITM (Man-in-the-Middle) meskipun menggunakan CA yang dipercaya sistem

---

## Saran yang Diterapkan

### ✅ 1. Tampilan Aplikasi Menarik

- Material Design Components (MaterialCardView, CollapsingToolbarLayout, FAB, BottomNavigationView)
- Shimmer loading effect pada halaman list
- Gradient overlay pada backdrop di detail
- Warna konsisten menggunakan colors.xml
- Margin, padding, dan ukuran komponen sesuai standar

### ✅ 2. Fitur Tambahan — Search

- Halaman Search dengan debounce 500ms menggunakan `StateFlow` + `flatMapLatest`
- Real-time search saat user mengetik tanpa spam request

### ✅ 3. Tiga Model Terpisah (Presentation, Domain, Data)

- **Data Model**: `MovieResponse.kt` (Retrofit) & `MovieEntity.kt` (Room)
- **Domain Model**: `Movie.kt` (pure Kotlin, tanpa dependency Android/library)
- **Presentation Model**: `MovieItem.kt` (UI-specific, berisi formatted string seperti rating "8.5", voteCount "2101 votes", URL gambar lengkap)
- `DataMapper.kt` menangani konversi: Response → Entity → Domain → Presentation

### ✅ 4. Reactive Programming untuk UI

- Search menggunakan `StateFlow` + `debounce(500ms)` + `flatMapLatest` (reactive UI)
- Semua data dari repository menggunakan Coroutine `Flow`
- ViewModel menggunakan `.asLiveData()` untuk konversi Flow ke LiveData
- Repository pattern: network → cache → emit ke UI

### ✅ 5. Dependency Injection dengan Scope Tepat

- `single` — Database, NetworkService, Repository, DataSource (singleton, 1 instance seumur proses)
- `factory` — UseCase (dibuat fresh setiap kali dibutuhkan)
- `viewModel` — ViewModel (lifecycle-aware, destroyed bersama Activity/Fragment)
- Dynamic Feature (Favorite) load module secara lazy via `loadKoinModules` di `onAttach()`

### ✅ 6. Jetpack Navigation Component

- `DynamicNavHostFragment` untuk support Dynamic Feature navigation
- `BottomNavigationView` terintegrasi penuh dengan NavController
- `include-dynamic` di nav_graph.xml untuk navigasi ke Favorite module
- Back stack dikelola otomatis oleh Navigation Component

### ✅ 7. Unit Test

**Lokasi:** `core/src/test/java/com/dicoding/moviecatalogue/core/`

| File                            | Jumlah Test | Cakupan                                                                |
| ------------------------------- | ----------- | ---------------------------------------------------------------------- |
| `utils/DataMapperTest.kt`       | 16          | Semua arah mapping (response↔entity↔domain), null handling, round-trip |
| `utils/ResourceTest.kt`         | 7           | Semua state Resource (Loading, Success, Error)                         |
| `domain/MovieInteractorTest.kt` | 13          | Semua use case method, delegasi ke repository                          |
| `data/MovieRepositoryTest.kt`   | 11          | Cache-first logic, remote fallback, error state, favorites             |
| **Total**                       | **47**      |                                                                        |

---

## Struktur Arsitektur

```
app/                           → Presentation Layer
├── presentation/
│   ├── home/                  → HomeFragment + HomeViewModel
│   ├── search/                → SearchFragment + SearchViewModel (StateFlow + debounce)
│   └── detail/                → DetailActivity + DetailViewModel
└── di/AppModule.kt            → useCaseModule (factory) + viewModelModule (viewModel)

core/                          → Android Library — Data + Domain Layer
├── data/
│   ├── source/local/          → Room + SQLCipher, MovieEntity, MovieDao, LocalDataSource
│   ├── source/remote/         → Retrofit + CertificatePinner, MovieResponse, ApiService, RemoteDataSource
│   └── MovieRepository.kt     → Implements IMovieRepository (network-first + cache)
├── domain/
│   ├── model/Movie.kt         → Domain Model (pure Kotlin)
│   ├── repository/IMovieRepository.kt
│   └── usecase/               → MovieUseCase interface + MovieInteractor
├── ui/
│   ├── MovieItem.kt           → Presentation Model + mapper toMovieItem()
│   └── MovieAdapter.kt        → Shared RecyclerView Adapter
├── utils/
│   ├── Resource.kt            → Sealed class (Loading / Success / Error)
│   └── DataMapper.kt          → Mapper antar 3 layer
└── di/CoreModule.kt           → databaseModule + networkModule + repositoryModule

favorite/                      → Dynamic Feature Module (install-time)
├── presentation/
│   ├── FavoriteFragment.kt    → load Koin module di onAttach(), ViewModel manual factory
│   ├── FavoriteViewModel.kt
│   └── FavoriteViewModelFactory.kt
├── di/FavoriteModule.kt       → favoriteModule (loaded on-demand saat fragment attach)
└── res/navigation/            → favorite_nav_graph.xml

.github/workflows/android.yml  → CI: test → build APK → lint
```

---

## Teknologi yang Digunakan

- **Language**: Kotlin
- **Architecture**: Clean Architecture (3 layer: Data, Domain, Presentation)
- **Modularization**: Android Library `core` + Dynamic Feature `favorite`
- **DI**: Koin 3.5.3
- **Reactive**: Coroutine Flow + StateFlow + LiveData
- **Database**: Room 2.6.1 + **SQLCipher 4.5.4** (enkripsi AES-256)
- **Network**: Retrofit 2 + OkHttp + **CertificatePinner** + Gson
- **Image Loading**: Coil 2.5.0
- **Navigation**: Jetpack Navigation Component dengan Dynamic Features
- **UI**: Material Design Components, ViewBinding, Shimmer
- **Security**: R8/ProGuard obfuscation, SQLCipher encryption, OkHttp CertificatePinner
- **Memory**: LeakCanary 2.12 (debug build)
- **Testing**: JUnit 4, Mockito 5.8, kotlinx-coroutines-test
- **CI**: GitHub Actions

## API

- **TheMovieDB** — https://www.themoviedb.org/
- Endpoint yang digunakan:
  - `GET /movie/popular` — Halaman Home
  - `GET /movie/{id}` — Halaman Detail
  - `GET /search/movie` — Halaman Search
