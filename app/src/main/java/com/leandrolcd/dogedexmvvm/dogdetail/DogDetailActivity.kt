package com.leandrolcd.dogedexmvvm.dogdetail

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.load
import com.leandrolcd.dogedexmvvm.Dog
import com.leandrolcd.dogedexmvvm.R
import com.leandrolcd.dogedexmvvm.databinding.ActivityDogDetailBinding

class DogDetailActivity : AppCompatActivity() {
    companion object {
        const val DOG_KEY = "dog"
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(DOG_KEY, Dog::class.java)
        } else {
            intent.getParcelableExtra<Dog>(DOG_KEY)
        }

        if (dog == null) {
            Toast.makeText(this, getString(R.string.dog_not_found), Toast.LENGTH_LONG)
            finish()
            return
        }
        binding.dogIndex.text = getString(R.string.dog_index_format, dog.index)
        binding.lifeExpectancy.text = getString(R.string.dog_life_format, dog.lifeExpectancy)
        binding.dogImage.load(dog.imageUrl)
        binding.dog = dog
        binding.closeButton.setOnClickListener {
            finish()
        }
    }
}