package com.example.weathometer.ui.fragments

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.stuplan.sqlite.DatabaseHelper
import com.example.weathometer.R
import com.example.weathometer.Utility.*
import com.example.weathometer.Utility.Notification
import com.example.weathometer.databinding.FragmentNotificationsBinding
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var dbHelper : DatabaseHelper
    private lateinit var preferenceMangager: PreferenceMangager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        preferenceMangager = PreferenceMangager(requireContext())

        val location = preferenceMangager.getString(PreferenceMangager.DEFAULT_CITY)

        binding.tvDefaultCity.text=location

        binding.tvDefaultCity.setOnClickListener {
            changeCity()
        }

        createNotificationChannel()

        binding.switchSettingsNotification.isChecked = preferenceMangager.getBoolean(PreferenceMangager.KEY_NOTIFICATION_ENABLED)

        binding.switchSettingsNotification.setOnCheckedChangeListener { switchView, isChecked ->
            if (isChecked) {
                // The switch enabled
                Toast.makeText(requireContext(), "Notification turned on", Toast.LENGTH_SHORT).show()
                scheduleNotification()
                preferenceMangager.putBoolean(PreferenceMangager.KEY_NOTIFICATION_ENABLED,isChecked)

            } else {
                // The switch disabled
                Toast.makeText(requireContext(), "Notification turned off", Toast.LENGTH_SHORT).show()
                preferenceMangager.putBoolean(PreferenceMangager.KEY_NOTIFICATION_ENABLED,false)
                preferenceMangager.putLong(PreferenceMangager.KEY_NOTIFICATION_TIME,0)
            }
        }

        binding.btnClearSettings.setOnClickListener {
            dbHelper.clearAllData()
            Toast.makeText(requireContext(), "All data cleared.", Toast.LENGTH_SHORT).show()
        }

        binding.tvContactWeathometerSettings.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:weathometerappmdev@gmail.com")
            }
            startActivity(Intent.createChooser(emailIntent, "Send feedback to us"))
        }

        val timeLong = preferenceMangager.getLong(PreferenceMangager.KEY_NOTIFICATION_TIME)

        if(timeLong>0){
            val date = Date(timeLong)
            val format = SimpleDateFormat("HH:mm")

            binding.tvTimeSettings.text=format.format(date)
        }

        binding.tvTimeSettings.setOnClickListener {
            val timePicker = TimePickerDialog(
                // pass the Context
                requireContext(),
                object : TimePickerDialog.OnTimeSetListener{
                    override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {
                        val calendar= Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        binding.tvTimeSettings.text=hourOfDay.toString()+" : "+minute.toString()
                        preferenceMangager.putLong(PreferenceMangager.KEY_NOTIFICATION_TIME,calendar.timeInMillis)
                    }
                },
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                false)
            timePicker.show()
        }
    }

    private fun changeCity() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .create()
        val dialogView = layoutInflater.inflate(R.layout.change_city_ui,null)
        builder.setView(dialogView)

        val etChangeCity = dialogView.findViewById<EditText>(R.id.et_change_city)
        val btnChangeCity = dialogView.findViewById<Button>(R.id.btn_change_city)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel_change_city)

        btnChangeCity.setOnClickListener {
            val cityNameFromET = etChangeCity.text.toString()
            if (cityNameFromET.isNullOrEmpty()){
                Toast.makeText(requireContext(), "City name is empty.", Toast.LENGTH_SHORT).show()
            }else{
                binding.tvDefaultCity.text=etChangeCity.text.toString()
                preferenceMangager.putString(PreferenceMangager.DEFAULT_CITY,cityNameFromET)

            }
            builder.dismiss()
        }

        btnCancel.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification()
    {
        val intent = Intent(requireContext(), Notification::class.java)
        intent.putExtra(titleExtra,"Hey, There!")
        intent.putExtra(messageExtra,"Get the weather updates for now.")
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = preferenceMangager.getLong(PreferenceMangager.KEY_NOTIFICATION_TIME)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val name= "Daily Notification"
        val desc = "Remainder about the due tasks"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId,name,importance)
        channel.description=desc
        val notificationManager=requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}