package net.sourov.bondhu.Model;

public class Contacts {

    String name,number,dateOfBirth,address,imageUrl,uniqueID,fd_added_date;

    public Contacts(String name, String number, String dateOfBirth, String address, String imageUrl, String uniqueID, String fd_added_date) {
        this.name = name;
        this.number = number;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.imageUrl = imageUrl;
        this.uniqueID = uniqueID;
        this.fd_added_date = fd_added_date;
    }

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getFd_added_date() {
        return fd_added_date;
    }

    public void setFd_added_date(String fd_added_date) {
        this.fd_added_date = fd_added_date;
    }
}
