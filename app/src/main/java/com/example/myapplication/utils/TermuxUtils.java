package com.example.myapplication.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

/**
 * 使用termux命令行的工具，由于android限制，Android API版本不能超过28
 */
public class TermuxUtils {
    public static void installJittor() {
        try{
            Process process = Runtime.getRuntime().exec("./termux/bin/sh", null, new File("/data/data/com.example.myapplication/"));
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
//            Log.d("runVse", "1");
            os.writeBytes("mkdir -p .cache/jittor/default/clang" + "\n");
            os.writeBytes("cd .cache/jittor/default/clang" + "\n");
            os.writeBytes("ln -s /data/data/com.example.myapplication/termux/lib/crtbegin_so.o crtbegin_so.o" + "\n");
            os.writeBytes("ln -s /data/data/com.example.myapplication/termux/lib/crtend_so.o crtend_so.o" + "\n");
            os.writeBytes("ln -s jit_utils_core.cpython-39.so libjit_utils_core.so" + "\n");
            os.writeBytes("ln -s jittor_core.cpython-39.so libjittor_core.so" + "\n");
            os.writeBytes("export LD_LIBRARY_PATH=/data/data/com.example.myapplication/termux/lib:/data/data/com.example.myapplication/.cache/jittor/default/clang" + "\n");
            os.writeBytes("export PATH=/data/data/com.example.myapplication/termux/bin:$PATH" + "\n");
            os.writeBytes("export cc_path=clang" + "\n");
            os.writeBytes("export TMPDIR=/data/data/com.example.myapplication/tempfile" + "\n");
            os.writeBytes("echo test \n");
            os.writeBytes("export PYTHONPATH=/data/data/com.example.myapplication/myjittor" + "\n");
            os.writeBytes("export C_INCLUDE_PATH=/data/data/com.example.myapplication/.cache/jittor/default/clang:/data/data/com.example.myapplication/termux/include:/data/data/com.example.myapplication/termux/include/c++/v1" + "\n");
            os.writeBytes("export CPLUS_INCLUDE_PATH=/data/data/com.example.myapplication/.cache/jittor/default/clang:/data/data/com.example.myapplication/termux/include:/data/data/com.example.myapplication/termux/include/c++/v1" + "\n");
            os.writeBytes("python -c 'print(123)'" + "\n");
            os.writeBytes("cd /data/data/com.example.myapplication/\n");
//            os.writeBytes("ls" + "\n");
//            System.out.println("ls");
//            System.out.println("ls");
            os.writeBytes("is_mobile=1 use_c=0 python -c 'import jittor'" + "\n");
            os.writeBytes("is_mobile=1 use_c=0 python -c 'import jittor'" + "\n");
            os.writeBytes("exit" + "\n");
            os.flush();
            String temp;
            String temp2;
            String show = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((temp = reader.readLine()) != null) {
                show = show + temp + "\n";
                Log.d("INFO", temp);
//                System.out.println(temp);
            }
            BufferedReader errorreader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((temp2 = errorreader.readLine()) != null) {
                show = show + temp2 + "\n";
                Log.d("ERROR", temp2);
//                System.out.println(temp2);
            }
            process.waitFor();
            Log.d("runINIT", "finish INIT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runVse(String inDir){
        Long t1 = System.currentTimeMillis();
        try{
            Process process = Runtime.getRuntime().exec("./termux/bin/sh", null, new File("/data/data/com.example.myapplication/"));
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
//            Log.d("runVse", "1");
//            os.writeBytes("mkdir -p .cache/jittor/default/clang" + "\n");
//            os.writeBytes("cd .cache/jittor/default/clang" + "\n");
//            os.writeBytes("ln -s /data/data/com.example.myapplication/termux/lib/crtbegin_so.o crtbegin_so.o" + "\n");
//            os.writeBytes("ln -s /data/data/com.example.myapplication/termux/lib/crtend_so.o crtend_so.o" + "\n");
//            os.writeBytes("ln -s jit_utils_core.cpython-39.so libjit_utils_core.so" + "\n");
//            os.writeBytes("ln -s jittor_core.cpython-39.so libjittor_core.so" + "\n");
            os.writeBytes("export LD_LIBRARY_PATH=/data/data/com.example.myapplication/termux/lib:/data/data/com.example.myapplication/.cache/jittor/default/clang" + "\n");
            os.writeBytes("export PATH=/data/data/com.example.myapplication/termux/bin:$PATH" + "\n");
            os.writeBytes("export cc_path=clang" + "\n");
            os.writeBytes("export TMPDIR=/data/data/com.example.myapplication/tempfile" + "\n");
            os.writeBytes("echo test \n");
            os.writeBytes("export PYTHONPATH=/data/data/com.example.myapplication/myjittor" + "\n");
            os.writeBytes("export C_INCLUDE_PATH=/data/data/com.example.myapplication/.cache/jittor/default/clang:/data/data/com.example.myapplication/termux/include:/data/data/com.example.myapplication/termux/include/c++/v1" + "\n");
            os.writeBytes("export CPLUS_INCLUDE_PATH=/data/data/com.example.myapplication/.cache/jittor/default/clang:/data/data/com.example.myapplication/termux/include:/data/data/com.example.myapplication/termux/include/c++/v1" + "\n");
            os.writeBytes("python -c 'print(123)'" + "\n");
            String comm = "export input_json=\"" + inDir + "/input.json\"" + "\n";
            System.out.println(comm);
            os.writeBytes(comm);
            os.writeBytes("cd /data/data/com.example.myapplication/vsepp-jittor/\n");
            os.writeBytes("is_mobile=1 use_c=0 python run.py\n");
            os.writeBytes("exit" + "\n");
            os.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                Log.d("INFO", temp);
//                System.out.println(temp);
            }
            String temp2;
            BufferedReader errorreader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((temp2 = errorreader.readLine()) != null) {
                Log.d("ERROR", temp2);
//                System.out.println(temp2);
            }
            process.waitFor();
            Long t2 = System.currentTimeMillis();
            Log.d("runVSE", String.format("finish vse in %d s", (t2-t1)/1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
