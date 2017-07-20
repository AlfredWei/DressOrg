package org.dress.mydress.model;

/**
 * Created by j-weishiyi on 2017/6/12.
 */
import org.dress.mydress.model.User;
import org.dress.mydress.model.Wardrobe;
import org.dress.mydress.model.Cloth;
import org.dress.mydress.model.ClothType;

public class Home {
    public User user_info;
    public Wardrobe wardrobe_info;

    public Home()
    {
        user_info = new User();
        wardrobe_info = new Wardrobe();
    }
}
