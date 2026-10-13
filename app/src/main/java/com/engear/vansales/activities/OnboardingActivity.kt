package com.engear.vansales.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.engear.vansales.adapter.ViewPagerAdapter
import com.engear.vansales.base.BaseActivity
import com.engear.vansales.databinding.ActivityOnboardingBinding
import com.engear.vansales.fragments.OnBoard1Fragment
import com.engear.vansales.fragments.OnBoard2Fragment
import com.engear.vansales.fragments.OnBoard3Fragment
import com.engear.vansales.utils.Utils

class OnboardingActivity : BaseActivity() {

    lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(
            LayoutInflater.from(this),
            baseBinding.frameContainer,
            true
        )
        init()
    }

    private fun init() {
        Utils.logData(
            this@OnboardingActivity,
            "On Boarding Screen "
        )
        makeStatusBarTransparent()
        setFullScreen()

        setStatePageAdapter()

        binding.tvSkip.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setStatePageAdapter() {
        val myViewPageStateAdapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        myViewPageStateAdapter.addFragment(OnBoard1Fragment(), "")
        myViewPageStateAdapter.addFragment(OnBoard2Fragment(), "")
        myViewPageStateAdapter.addFragment(OnBoard3Fragment(), "")
        binding.pager.adapter = myViewPageStateAdapter
        binding.dotsIndicator.setViewPager(binding.pager)

        binding.pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.lytContinue.visibility = View.VISIBLE
                    binding.tvSkip.visibility = View.GONE
                } else {
                    binding.lytContinue.visibility = View.INVISIBLE
                    binding.tvSkip.visibility = View.VISIBLE
                }
            }
        })
    }
}