package duobk_constructor.helpers;

import duobk_constructor.model.DuoBook;
import org.springframework.web.multipart.MultipartFile;

public class UploadedDuoBook extends DuoBook {
    private MultipartFile uploadedImage; // just helper for updating Book's image. (used in /books/update)

    public MultipartFile getUploadedImage() {
        return uploadedImage;
    }

    public void setUploadedImage(MultipartFile uploadedImage) {
        this.uploadedImage = uploadedImage;
    }
}
