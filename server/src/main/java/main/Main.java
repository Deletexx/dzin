package main;

import com.blade.Blade;

public class Main  {
    public static void main(String[] args) {
        Blade blade = Blade.of();
        // Create a route: /user/:uid
        blade.get("/comment/:uid", ctx -> {
            Integer uid = ctx.pathInt("uid");
            ctx.text("uid : " + uid);
        });

        // Start blade
        blade.start();
    }
}


