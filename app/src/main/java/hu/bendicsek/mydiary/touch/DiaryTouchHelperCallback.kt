package hu.bendicsek.mydiary.touch

interface DiaryTouchHelperCallback {
    fun onDismissed(position: Int)
    fun onItemMoved(fromPosition: Int, toPosition: Int)
}