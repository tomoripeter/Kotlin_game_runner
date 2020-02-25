import javafx.scene.image.Image

class AnimatedImage(private val duration: Double) {
    val images = ArrayList<Image>()

    fun getAnimatedImage(time: Double): Image {
        val index = ((time % (images.size * duration)) / duration)
        return images[index.toInt()]
    }
}