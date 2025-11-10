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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.keylab.mobile.R
import com.keylab.mobile.databinding.ActivityProfileBinding
import com.keylab.mobile.databinding.BottomSheetPhotoOptionsBinding
import java.io.File

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
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
        // TODO: Cargar datos reales del usuario desde SharedPreferences o Supabase Auth
        binding.apply {
            tvUserName.text = "Usuario KeyLab"
            tvUserEmail.text = "usuario@keylab.com"
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
            
            // Mis pedidos
            cardOrders.setOnClickListener {
                // TODO: Navegar a historial de pedidos
            }
            
            // Direcciones
            cardAddresses.setOnClickListener {
                // TODO: Navegar a gestión de direcciones
            }
            
            // Métodos de pago
            cardPayments.setOnClickListener {
                // TODO: Navegar a métodos de pago
            }
            
            // Favoritos
            cardFavorites.setOnClickListener {
                // TODO: Navegar a productos favoritos
            }
            
            // Configuración
            cardSettings.setOnClickListener {
                // TODO: Navegar a configuración
            }
            
            // Ayuda
            cardHelp.setOnClickListener {
                // TODO: Abrir sección de ayuda
            }
            
            // Cerrar sesión
            btnLogout.setOnClickListener {
                // TODO: Limpiar sesión y volver a LoginActivity
                val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
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
