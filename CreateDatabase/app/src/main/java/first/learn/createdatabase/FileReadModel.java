package first.learn.createdatabase;

public class FileReadModel {



    String id, name, imgString;

    public FileReadModel() {
    }

    public FileReadModel(String id, String name, String imgString) {
        this.id = id;
        this.name = name;
        this.imgString = imgString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }

}
