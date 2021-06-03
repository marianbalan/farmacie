package controller;

import model.User;
import service.Service;

public interface RolController {
    void setService(Service service);
    void setUser(User user);

}
