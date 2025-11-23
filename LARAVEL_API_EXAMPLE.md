# Contoh Laravel API Controller untuk Perpustakaan

## BukuController.php

```php
<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Buku;
use Illuminate\Http\Request;

class BukuController extends Controller
{
    /**
     * GET /api/buku
     * Mendapatkan semua buku
     */
    public function index()
    {
        try {
            $buku = Buku::orderBy('created_at', 'desc')->get();
            
            return response()->json([
                'success' => true,
                'message' => 'Data buku berhasil diambil',
                'data' => $buku
            ], 200);
            
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengambil data buku',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * GET /api/buku/{id}
     * Mendapatkan detail buku berdasarkan ID
     */
    public function show($id)
    {
        try {
            $buku = Buku::find($id);
            
            if (!$buku) {
                return response()->json([
                    'success' => false,
                    'message' => 'Buku tidak ditemukan',
                    'data' => null
                ], 404);
            }
            
            return response()->json([
                'success' => true,
                'message' => 'Detail buku berhasil diambil',
                'data' => $buku
            ], 200);
            
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal mengambil detail buku',
                'error' => $e->getMessage()
            ], 500);
        }
    }

    /**
     * GET /api/buku/search?q=keyword
     * Mencari buku berdasarkan keyword
     */
    public function search(Request $request)
    {
        try {
            $keyword = $request->query('q');
            
            if (!$keyword) {
                return response()->json([
                    'success' => false,
                    'message' => 'Keyword pencarian tidak boleh kosong',
                    'data' => []
                ], 400);
            }
            
            $buku = Buku::where('judul', 'like', "%{$keyword}%")
                ->orWhere('penulis', 'like', "%{$keyword}%")
                ->orWhere('description', 'like', "%{$keyword}%")
                ->get();
            
            return response()->json([
                'success' => true,
                'message' => 'Hasil pencarian buku',
                'data' => $buku
            ], 200);
            
        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Gagal melakukan pencarian',
                'error' => $e->getMessage()
            ], 500);
        }
    }
}
```

## Routes (api.php)

```php
<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\BukuController;

// Buku Routes
Route::get('/buku', [BukuController::class, 'index']);
Route::get('/buku/{id}', [BukuController::class, 'show']);
Route::get('/buku/search', [BukuController::class, 'search']);
```

## Model Buku.php

```php
<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Buku extends Model
{
    use HasFactory;

    protected $table = 'bukus';
    
    protected $fillable = [
        'judul',
        'penulis',
        'tipe',
        'tahun_terbit',
        'description',
        'foto'
    ];

    protected $casts = [
        'tahun_terbit' => 'integer',
    ];
    
    // Accessor untuk foto URL lengkap
    public function getFotoAttribute($value)
    {
        if ($value && !filter_var($value, FILTER_VALIDATE_URL)) {
            return asset('storage/' . $value);
        }
        return $value;
    }
}
```

## Migration

```php
<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::create('bukus', function (Blueprint $table) {
            $table->id();
            $table->string('judul');
            $table->string('penulis');
            $table->enum('tipe', ['harian', 'tahunan']);
            $table->year('tahun_terbit');
            $table->text('description');
            $table->string('foto')->nullable();
            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('bukus');
    }
};
```

## Seeder (BukuSeeder.php)

```php
<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Buku;

class BukuSeeder extends Seeder
{
    public function run()
    {
        $bukus = [
            [
                'judul' => 'Laskar Pelangi',
                'penulis' => 'Andrea Hirata',
                'tipe' => 'tahunan',
                'tahun_terbit' => 2005,
                'description' => 'Novel tentang kehidupan sekelompok anak Melayu Belitong yang bersekolah di SD Muhammadiyah.',
                'foto' => 'https://example.com/laskar-pelangi.jpg'
            ],
            [
                'judul' => 'Bumi Manusia',
                'penulis' => 'Pramoedya Ananta Toer',
                'tipe' => 'tahunan',
                'tahun_terbit' => 1980,
                'description' => 'Novel sejarah yang mengisahkan perjalanan tokoh Minke di era kolonial Belanda.',
                'foto' => 'https://example.com/bumi-manusia.jpg'
            ],
            [
                'judul' => 'Sang Pemimpi',
                'penulis' => 'Andrea Hirata',
                'tipe' => 'tahunan',
                'tahun_terbit' => 2006,
                'description' => 'Kelanjutan kisah Laskar Pelangi tentang tiga remaja Melayu yang bermimpi.',
                'foto' => 'https://example.com/sang-pemimpi.jpg'
            ]
        ];

        foreach ($bukus as $buku) {
            Buku::create($buku);
        }
    }
}
```

## Testing API

### Test dengan Postman/Thunder Client

1. **Get All Books**
   - Method: GET
   - URL: http://localhost:8000/api/buku
   - Expected Response:
   ```json
   {
     "success": true,
     "message": "Data buku berhasil diambil",
     "data": [ /* array of books */ ]
   }
   ```

2. **Get Book Detail**
   - Method: GET
   - URL: http://localhost:8000/api/buku/1
   - Expected Response:
   ```json
   {
     "success": true,
     "message": "Detail buku berhasil diambil",
     "data": { /* book object */ }
   }
   ```

3. **Search Books**
   - Method: GET
   - URL: http://localhost:8000/api/buku/search?q=laskar
   - Expected Response:
   ```json
   {
     "success": true,
     "message": "Hasil pencarian buku",
     "data": [ /* filtered books */ ]
   }
   ```

## CORS Configuration (config/cors.php)

Jika mengalami masalah CORS, tambahkan konfigurasi ini:

```php
<?php

return [
    'paths' => ['api/*'],
    'allowed_methods' => ['*'],
    'allowed_origins' => ['*'],
    'allowed_origins_patterns' => [],
    'allowed_headers' => ['*'],
    'exposed_headers' => [],
    'max_age' => 0,
    'supports_credentials' => false,
];
```

## .env Configuration

```env
APP_URL=http://localhost:8000

# Database
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=perpustakaan
DB_USERNAME=root
DB_PASSWORD=
```

## Run Laravel

```bash
# Install dependencies
composer install

# Generate key
php artisan key:generate

# Run migrations
php artisan migrate

# Run seeder (optional)
php artisan db:seed --class=BukuSeeder

# Start server
php artisan serve
```

## Notes

- Pastikan Laravel berjalan di port 8000
- Untuk storage images, gunakan `php artisan storage:link`
- Format response harus sesuai dengan yang diharapkan Android app
- Gunakan proper HTTP status codes (200, 404, 500)
- Implementasi validation sesuai kebutuhan
