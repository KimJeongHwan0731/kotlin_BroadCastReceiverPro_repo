package com.example.broadcastreceiverpro

import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.BatteryManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.broadcastreceiverpro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. 브로드캐스트 리시버 만들어서 바로 배터리 정보를 획득함.
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = registerReceiver(null, intentFilter)

        // 2. 배터리 정보량을 체크함
        val extra_status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        Log.e("MainActivity", "${extra_status}")
        when(extra_status) {
            // 충전 정보 체크하는데, USB 충전중 | AC로 충전중
            BatteryManager.BATTERY_STATUS_CHARGING -> {
                when(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                    BatteryManager.BATTERY_PLUGGED_AC -> {
                        binding.ivBattery.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.ac))
                        binding.tvInfo.text = "PLUGGED_AC"
                    }
                    BatteryManager.BATTERY_PLUGGED_USB -> {
                        binding.ivBattery.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.usb))
                        binding.tvInfo.text = "PLUGGED_USB"
                    }
                    BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
                        binding.ivBattery.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.wireless))
                        binding.tvInfo.text = "PLUGGED_WIRELESS"
                    }
                    else -> {
                        binding.ivBattery.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.battery_full_24))
                        binding.tvInfo.text = "PULL CHARGING"
                    }
                }
            }
            // NO 충전중
            else -> {
                binding.ivBattery.setImageResource(R.drawable.battery_unknown_24)
                binding.tvInfo.text = "NO CHARGING"
            }
        }

        //배터리 잔여량을 계산 보여줌.
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        var percent = level!!.toFloat() / scale!!.toFloat() * 100
        binding.tvPercent.text = "${percent} %"

        //이벤트 처리(내가 만든 MyReceiver 불러서 Notification 알림 발생) : 부가적인 정보 배터리양 보내줌
        binding.btnCallReceiver.setOnClickListener {
            val intent = Intent(this, MyReceiver::class.java)
            intent.putExtra("batteryPercent", "${binding.tvPercent.text}")
            sendBroadcast(intent)
        }
    }
}