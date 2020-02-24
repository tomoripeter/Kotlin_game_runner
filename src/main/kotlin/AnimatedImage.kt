import javafx.scene.image.Image

class AnimatedImage(duration:Double) {
    val images = ArrayList<Image>()
    private val duration = duration

    fun getAnimatedImage(time:Double): Image
    {
        val index = ((time % (images.size * duration)) / duration)
        return images[index.toInt()]
    }
}