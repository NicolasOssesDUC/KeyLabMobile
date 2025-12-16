package com.keylab.mobile.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.keylab.mobile.R
import com.keylab.mobile.data.local.AppDatabase
import com.keylab.mobile.data.local.PreferencesManager
import com.keylab.mobile.databinding.ActivityProfileBinding
import com.keylab.mobile.databinding.BottomSheetPhotoOptionsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var database: AppDatabase
    private var currentPhotoUri: Uri? = null
    private var selectedImageUri: Uri? = null
    
    // Launcher para galería
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            loadProfileImage(it)
            Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Launcher para cámara
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            selectedImageUri = currentPhotoUri
            loadProfileImage(currentPhotoUri!!)
            Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Launcher para permisos de cámara
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        database = AppDatabase.getDatabase(this)
        
        setupToolbar()
        setupUserInfo()
        setupClickListeners()
    }
    
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupUserInfo() {
        val userId = preferencesManager.obtenerUserId()
        if (userId != -1) {
            lifecycleScope.launch {
                // Observar cambios en el usuario (Flow)
                database.usuarioDao().obtenerPorId(userId).collect { usuario ->
                    usuario?.let {
                        binding.tvUserName.text = it.nombre
                        binding.tvUserEmail.text = it.email
                        
                        // Actualizar inicial
                        if (it.nombre.isNotEmpty()) {
                            val cardView = binding.root.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardAvatar)
                            val textView = cardView?.findViewById<android.widget.TextView>(android.R.id.text1)
                            textView?.text = it.nombre.first().toString().uppercase()
                        }
                        
                        // Cargar foto si existe
                        if (!it.avatarUrl.isNullOrEmpty()) {
                            loadProfileImage(it.avatarUrl)
                        }
                    }
                }
            }
        } else {
            // Si no hay usuario válido, forzar logout
            logout()
        }
    }
    
    // Sobrecarga para cargar desde URL (String)
    private fun loadProfileImage(url: String) {
        val avatarCardView = binding.root.findViewById<com.google.android.material.card.MaterialCardView>(
            R.id.cardAvatar
        )
        
        avatarCardView?.let { cardView ->
            val textView = cardView.findViewById<android.widget.TextView>(android.R.id.text1)
            textView?.visibility = android.view.View.GONE
            
            var imageView = cardView.findViewById<android.widget.ImageView>(R.id.ivAvatar)
            if (imageView == null) {
                imageView = android.widget.ImageView(this).apply {
                    id = R.id.ivAvatar
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                cardView.addView(imageView)
            }
            
            Glide.with(this)
                .load(url)
                .circleCrop()
                .placeholder(R.drawable.ic_placeholder_image) 
                .error(R.drawable.ic_placeholder_image)
                .into(imageView)
        }
    }
    
    private fun setupClickListeners() {
        binding.apply {
            // Click en avatar para cambiar foto
            binding.root.findViewById<com.google.android.material.card.MaterialCardView>(
                R.id.cardAvatar
            )?.setOnClickListener {
                showPhotoOptionsBottomSheet()
            }
            
            // Mis pedidos (Navegación al historial de pedidos)
            cardOrders.setOnClickListener {
                val intent = Intent(this@ProfileActivity, OrderHistoryActivity::class.java)
                startActivity(intent)
            }
            
            // Direcciones
            cardAddresses.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, AddressListActivity::class.java))
            }
            
            // Configuración
            cardSettings.setOnClickListener {
                Toast.makeText(this@ProfileActivity, "Configuración próximamente", Toast.LENGTH_SHORT).show()
            }
            
            // Ayuda
            cardHelp.setOnClickListener {
                Toast.makeText(this@ProfileActivity, "Centro de ayuda próximamente", Toast.LENGTH_SHORT).show()
            }
            
            // Cerrar sesión
            btnLogout.setOnClickListener {
                logout()
            }
        }
    }
    
    private fun logout() {
        // Cerrar sesión de Google explícitamente
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // Limpiar sesión en preferencias
            preferencesManager.cerrarSesion()
            
            // Navegar al login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
    
    private fun showPhotoOptionsBottomSheet() {
        val bottomSheet = BottomSheetDialog(this)
        val bottomSheetBinding = BottomSheetPhotoOptionsBinding.inflate(LayoutInflater.from(this))
        bottomSheet.setContentView(bottomSheetBinding.root)
        
        bottomSheetBinding.apply {
            // Opción Cámara
            cardCamera.setOnClickListener {
                bottomSheet.dismiss()
                checkCameraPermissionAndOpen()
            }
            
            // Opción Galería
            cardGallery.setOnClickListener {
                bottomSheet.dismiss()
                openGallery()
            }
            
            // Cancelar
            btnCancel.setOnClickListener {
                bottomSheet.dismiss()
            }
        }
        
        bottomSheet.show()
    }
    
    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    private fun openCamera() {
        val photoFile = File.createTempFile(
            "profile_photo_${System.currentTimeMillis()}",
            ".jpg",
            cacheDir
        )
        
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        
        cameraLauncher.launch(currentPhotoUri)
    }
    
    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }
    
    private fun loadProfileImage(uri: Uri) {
        // Cargar imagen en el avatar usando Glide
        val avatarCardView = binding.root.findViewById<com.google.android.material.card.MaterialCardView>(
            R.id.cardAvatar
        )
        
        avatarCardView?.let { cardView ->
            // Buscar el TextView dentro del card y ocultarlo
            val textView = cardView.findViewById<android.widget.TextView>(
                android.R.id.text1
            )
            textView?.visibility = android.view.View.GONE
            
            // Agregar ImageView si no existe
            var imageView = cardView.findViewById<android.widget.ImageView>(R.id.ivAvatar)
            if (imageView == null) {
                imageView = android.widget.ImageView(this).apply {
                    id = R.id.ivAvatar
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                cardView.addView(imageView)
            }
            
            // Cargar imagen con Glide
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(imageView)
        }
        
        // TODO: Subir imagen a Supabase Storage
        // uploadProfileImage(uri)
    }
}
