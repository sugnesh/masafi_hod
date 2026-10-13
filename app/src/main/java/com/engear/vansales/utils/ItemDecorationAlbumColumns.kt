package com.engear.vansales.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecorationAlbumColumns(gridSpacingPx:Int, gridSize:Int): RecyclerView.ItemDecoration() {
    private var mSizeGridSpacingPx:Int = 0
    private var mGridSize:Int = 0
    private var mNeedLeftSpacing = false
    init{
        mSizeGridSpacingPx = gridSpacingPx
        mGridSize = gridSize
    }
    override fun getItemOffsets(outRect:Rect, view:View, parent:RecyclerView, state:RecyclerView.State) {
        val frameWidth = ((parent.getWidth() - mSizeGridSpacingPx.toFloat() * (mGridSize - 1)) / mGridSize) as Int
        val padding = parent.getWidth() / mGridSize - frameWidth
        val itemPosition = (view.getLayoutParams() as RecyclerView.LayoutParams).getViewAdapterPosition()
        if (itemPosition < mGridSize)
        {
            outRect.top = 0
        }
        else
        {
            outRect.top = mSizeGridSpacingPx
        }
        if (itemPosition % mGridSize == 0)
        {
            outRect.left = 0
            outRect.right = padding
            mNeedLeftSpacing = true
        }
        else if ((itemPosition + 1) % mGridSize == 0)
        {
            mNeedLeftSpacing = false
            outRect.right = 0
            outRect.left = padding
        }
        else if (mNeedLeftSpacing)
        {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx - padding
            if ((itemPosition + 2) % mGridSize == 0)
            {
                outRect.right = mSizeGridSpacingPx - padding
            }
            else
            {
                outRect.right = mSizeGridSpacingPx / 2
            }
        }
        else if ((itemPosition + 2) % mGridSize == 0)
        {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx / 2
            outRect.right = mSizeGridSpacingPx - padding
        }
        else
        {
            mNeedLeftSpacing = false
            outRect.left = mSizeGridSpacingPx / 2
            outRect.right = mSizeGridSpacingPx / 2
        }
        outRect.bottom = 0
    }
}