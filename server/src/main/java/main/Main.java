package main;

import com.blade.Blade;

public class Main  {
    public static void main(String[] args) {
        Blade blade = Blade.of();
        // Create a route: /user/:uid
        blade.get("/user/:uid", ctx -> {
            Integer uid = ctx.pathInt("uid");
            ctx.text("uid : " + uid);
        });

        // Create two parameters route
        blade.get("/users/:uid/post/:pid", ctx -> {
            Integer uid = ctx.pathInt("uid");
            Integer pid = ctx.pathInt("pid");
            String msg = "uid = " + uid + ", pid = " + pid;
            ctx.text(msg);
        });

        // Start blade
        blade.start();
    }
}


