package com.techteam.aqproad.Item

import android.Manifest
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.techteam.aqproad.R
import com.example.recyclerview.ComentarioAdapter
import com.techteam.aqproad.Home.ComentarioViewModel
import com.techteam.aqproad.Home.ComentarioRepository
import androidx.lifecycle.Observer
import com.techteam.aqproad.AudioService.TextToSpeechManager
import com.techteam.aqproad.AudioService.TextToSpeechService
import com.techteam.aqproad.Item.itemDB.RatingManagerDB
import com.techteam.aqproad.Map.MapFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.ContextCompat

class ItemFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //private lateinit var view: View
    private lateinit var carouselRecyclerView: RecyclerView
    private lateinit var recyView_comentarios: RecyclerView
    private lateinit var viewModel_comentarios: ComentarioViewModel
    private lateinit var adapter_comentarios: ComentarioAdapter
    private lateinit var editTextTextMultiLine: EditText  // Campo de texto para el comentario

    private lateinit var ratingBar: RatingBar
    private lateinit var ratingManagerDB: RatingManagerDB
    private lateinit var ratingPuntajeTotal: TextView

    //private var audioService: AudioService? = null Eliminamos cualquier rastro de audioservice
    //private var isBound = false Eliminamos cualquier rastro de audioservice
    private lateinit var btnPlayPause: ImageButton
    private lateinit var btnStop: ImageButton
    private lateinit var audioSeekBar: SeekBar
    private lateinit var tvCurrentTime: TextView
    private lateinit var tvTotalDuration: TextView
    private var buildingID: Int?=null
    private lateinit var textToSpeechManager: TextToSpeechManager

    // audio también
    private var ttsService: TextToSpeechService? = null
    private var isBound = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val ARG_BUILDING_ID = "building_id"

        fun newInstance(buildingId: Int): ItemFragment { //
            val fragment = ItemFragment()
            val args = Bundle() // creando bundle
            args.putInt(ARG_BUILDING_ID, buildingId) //agregando id de la edificacion al bundle
            fragment.arguments = args //asignando el bundle al fragment
            return fragment
        }
    }

    private val ttsServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TextToSpeechService.LocalBinder
            ttsService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ttsService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        ratingManagerDB = RatingManagerDB()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item, container, false)
        textToSpeechManager = TextToSpeechManager(view.context)
        val title = arguments?.getString("title") ?: ""
        val description = arguments?.getString("description") ?: ""
        val img = arguments?.getInt("img") ?: 0
        val imgString = arguments?.getString("imgUrl") ?: ""
        val sitCro = arguments?.getBoolean("Croquis") ?: false
        val imgPlano = view.findViewById<ImageView>(R.id.img_plano)

        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        val showMap = view.findViewById<TextView>(R.id.text_show_map)

        if(!sitCro){
            imgPlano.setImageResource(R.drawable.selector_nomap)
        }

        showMap.setOnClickListener{
            val mapFragment = MapFragment()

            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.main_container, mapFragment)
                .addToBackStack(null)
                .commit()
        }
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        setupUI(view, title, description,img, imgString)
        setupObservers(img)

        return view
    }

    private val PREFIJO = "https://raw.githubusercontent.com/rvelizs/imagenesAQPROAD/refs/heads/main/"

    private fun createListImages(ids: String): List<String> {
        return ids.split(",").map { "$PREFIJO${it.trim()}" }
    }


    private fun setupUI(view: View, title: String, description: String, img: Int, imgString: String) {
        //configurar RecyclerView del carrusel
        carouselRecyclerView = view.findViewById(R.id.recyclerCarousel)
        setupCarouselRecyclerView(imgString)
        showToast("Este es el ID del sitio $img")

        //configurar RecyclerView de comentarios
        recyView_comentarios = view.findViewById(R.id.recyView_comentarios)
        recyView_comentarios.layoutManager = LinearLayoutManager(requireContext())

        // EditText para nuevo comentario
        editTextTextMultiLine = view.findViewById(R.id.editTextTextMultiLine)  // EditText para comentarios

        // RatingBar y lógica asociada
        ratingPuntajeTotal = view.findViewById(R.id.txtPun)
        ratingBar = view.findViewById(R.id.ratingCalif)
        ratingManagerDB.getActualRatingBuild(img) { message, puntaje ->
            Log.d("SETEANDO PUNTAJE", "Puntaje de build con id: ${img}, es ${puntaje}")
            if (puntaje != null) {
                ratingPuntajeTotal.text = "${puntaje.toFloat()} (15 reseñas)"
            } else {
                message.let { it ->
                    if (it != null) {
                        showToast(it)
                    }
                }
            }
        }

        // elementos audio service
        btnPlayPause = view.findViewById(R.id.btnPlayPause)
        btnStop = view.findViewById(R.id.btnStop)
        audioSeekBar = view.findViewById(R.id.audioSeekBar)
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime)
        tvTotalDuration = view.findViewById(R.id.tvTotalDuration)

        btnPlayPause.setOnClickListener {
            //startTextToSpeechService(description)

            if (isTtsPlaying()) {
                // ...
                pauseTtsPlayback()
                btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                // ...
                btnPlayPause.setImageResource(R.drawable.ic_pause)
                startTtsService(description)
                //btnPlayPause.setImageResource(R.drawable.ic_pause)
            }
        }

        btnStop.setOnClickListener {
            // ...
            stopTtsService()
            btnPlayPause.setImageResource(R.drawable.ic_play)
            audioSeekBar.progress = 0
            tvCurrentTime.text = "0:00"
        }

        audioSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                /* if (fromUser && isBound) {
                     audioService?.mediaPlayer?.seekTo(progress)
                 }*/
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        setupRatingBar(img)

        val btnSendComment: Button = view.findViewById(R.id.button_send_comment)
        btnSendComment.setOnClickListener{handleSendComment(img)}

        val btnExpand: ImageButton = view.findViewById(R.id.btn_expand)
        val sitCro = arguments?.getBoolean("Croquis") ?: false
        btnExpand.setOnClickListener{
            if(sitCro){
                openCroquisFragment()
            }
        }

        // Actualizar los TextView con los datos recibidos
        view.findViewById<TextView>(R.id.txtTitle).text = title
        view.findViewById<TextView>(R.id.txtDes).text = description
        //startTextToSpeechService(description)
    }

    private fun stopTtsService() {
        if (!isTtsPlaying()) {
            val intent = Intent(requireContext(), TextToSpeechService::class.java).apply {
                action = TextToSpeechService.ACTION_STOP
            }
            requireContext().startService(intent)
        }
    }

    private fun pauseTtsPlayback() {
        val intent = Intent(requireContext(), TextToSpeechService::class.java).apply {
            action = TextToSpeechService.ACTION_PAUSE
        }
        requireContext().startService(intent)
    }

    private fun startTtsService(description: String) {
        val intent = Intent(requireContext(), TextToSpeechService::class.java).apply {
            action = TextToSpeechService.ACTION_START
            putExtra(TextToSpeechService.EXTRA_DESCRIPTION, description)
        }
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun startTextToSpeechService(description: String) {
        textToSpeechManager.initialize { isInitialized ->
            if (isInitialized) {

                // TTS Listo, puedes comenzar a usar speak() aqui
                Log.d("TTS", "TTS Inicializado")
                val textToSpeak = description
                textToSpeechManager.speak(textToSpeak)

            } else {
                // Manejar el fallo de inicialización
                Log.e("TTS", "Fallo la inicializacion")
            }
        }
        /*val startIntent = Intent(requireContext(), TextToSpeechService::class.java).apply {
            action = TextToSpeechService.ACTION_START
        }
        requireContext().startService(startIntent)
        val speakIntent = Intent(requireContext(), TextToSpeechService::class.java).apply {
            action = TextToSpeechService.ACTION_SPEAK
            putExtra(TextToSpeechService.EXTRA_TEXT, description)
        }
        requireContext().startService(speakIntent)*/
    }


    private fun setupCarouselRecyclerView(imgString: String) {
        val images = getImagesGaleria(imgString)
        carouselRecyclerView.adapter = CarouselAdapter(images)
    }

    private fun getImagesGaleria(imgString: String) : List<String> { //aqui debe configurarse otro repositorio para als imagenes respectivas a la edificacion
        return createListImages(imgString)
    }

    private fun setupObservers(sitId: Int) {
        val repository = ComentarioRepository()
        viewModel_comentarios = ComentarioViewModel(repository)

        // Inicializa el adaptador con una lista vacía
        adapter_comentarios = ComentarioAdapter(listOf())
        recyView_comentarios.adapter = adapter_comentarios

        // Observa los datos y actualiza el adaptador
        viewModel_comentarios.comentarios.observe(viewLifecycleOwner, Observer { comentarios ->
            adapter_comentarios.updateData(comentarios) // Actualiza el adaptador con los datos
        })

        // Carga los comentarios
        viewModel_comentarios.loadComentarios(sitId)
    }

    private fun setupRatingBar(sitId: Int) { //falta obtener el rating de la edificacion respectiva
        var isResettingRating = false
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            if (isResettingRating) return@setOnRatingBarChangeListener

            if (isUserLoggedIn()) {
                isUserAtLocation { isAtLocation ->
                    if (!isAtLocation) { //cambiando a false para testear !isAtLocation
                        showToast("Debe estar en la ubicación de la edificación para calificar.")
                        isResettingRating = true
                        ratingBar.rating = 0f
                        isResettingRating = false
                        return@isUserAtLocation
                    } else {
                        showToast("Puedes calificar.")
                        saveUserRating(rating, sitId)
                    }
                }
            } else {
                showToast("Debe iniciar sesión para calificar.")
                isResettingRating = true
                ratingBar.rating = 0f
                isResettingRating = false
                return@setOnRatingBarChangeListener
            }
        }
    }

    private fun handleSendComment(sitId: Int) { //es para agregar comentarios, falta enviarlo a la base de datos
        val commentText = editTextTextMultiLine.text.toString().trim()
        if (commentText.isNotEmpty()) {
            val usuarioActual = FirebaseAuth.getInstance().currentUser
            val userName = usuarioActual?.displayName ?: "Usuario"

            val comentarioTexto = commentText
            val usuarioNombre = userName // Puedes obtenerlo del contexto o de un campo
            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            viewModel_comentarios.addComentario(sitId, usuarioNombre, comentarioTexto, fechaActual)

            // Limpiar el campo de texto después de enviar el comentario
            editTextTextMultiLine.text.clear()

            // Mostrar un mensaje de éxito
            showToast("Comentario enviado")
        } else {
            showToast("Por favor escribe un comentario")
        }
    }

    private fun openCroquisFragment() { //Falta pasar id de la edificacion o el nombre para que la clase CrokisFragment renderice el correcto
        val fragment = CroquisFragment()
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun saveUserRating(rating: Float, sitId: Int) {
        val usuarioActual = FirebaseAuth.getInstance().currentUser?.displayName?:"Usuario"

        sitId.let {
            ratingManagerDB.saveRating(it, rating, usuarioActual){ mensaje ->
                showToast(mensaje)
            }
            showToast("Gracias por calificar con $rating estrellas.")
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    private fun isUserAtLocation(onResult: (Boolean) -> Unit) {
        val targetLatitude = -16.3911262
        val targetLongitude = -71.5122978

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            showToast("No tiene permisos")
            onResult(false)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity(), OnSuccessListener<Location> { location ->
            if (location != null) {
                val userLatitude = location.latitude
                val userLongitude = location.longitude
                val distance = FloatArray(1)
                Location.distanceBetween(userLatitude, userLongitude, targetLatitude, targetLongitude, distance)
                val tolerance = 60.0f
                onResult(distance[0] <= tolerance)
            } else {
                showToast("No puede obtener la ubicación")
                onResult(false)
            }
        })
    }

    private val audioFinishedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Cuando el audio ha terminado, actualiza el botón
            btnPlayPause.setImageResource(R.drawable.ic_play)
        }
    }

    override fun onStart() {
        super.onStart()
        // Enlaza el servicio
        val intent = Intent(requireContext(), TextToSpeechService::class.java)
        requireContext().bindService(intent, ttsServiceConnection, Context.BIND_AUTO_CREATE)

        // Registrar el BroadcastReceiver
        val filter = IntentFilter("com.example.tts.ACTION_AUDIO_FINISHED")
        requireContext().registerReceiver(audioFinishedReceiver, filter,
            Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        // Desenlaza el servicio
        if (isBound) {
            requireContext().unbindService(ttsServiceConnection)
            isBound = false
        }
        // Desregistrar el BroadcastReceiver
        requireContext().unregisterReceiver(audioFinishedReceiver)

        // Crear la notificación solo si el servicio está reproduciendo
        if (isTtsPlaying()) {
            val intent = Intent(requireContext(), TextToSpeechService::class.java).apply {
                action = TextToSpeechService.ACTION_CREATE_NOTIFICATION
                putExtra(TextToSpeechService.EXTRA_DESCRIPTION, "Descripción del lugar") // Cambia esto según sea necesario
            }
            requireContext().startService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Llamar a la acción para destruir la notificación
        val intent = Intent(requireContext(), TextToSpeechService::class.java).apply {
            action = TextToSpeechService.ACTION_DESTROY_NOTIFICATION
        }
        requireContext().startService(intent)
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        val packageName = requireContext().packageName
        for (process in appProcesses) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                process.processName == packageName) {
                return true
            }
        }
        return false
    }

    private fun isTtsPlaying(): Boolean {
        return ttsService?.isTtsPlaying() ?: false
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        val stopIntent = Intent(requireContext(), TextToSpeechService::class.java).apply {
            action = TextToSpeechService.ACTION_STOP
        }
        requireContext().startService(stopIntent)
        // textToSpeechManager.shutdown()
    }
}