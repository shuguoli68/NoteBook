package com.example.debut.controller

import com.example.debut.base.MyResponse
import org.springframework.ui.Model
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
import java.io.*
import javax.servlet.http.HttpServletRequest

@RestController
class FileController {
//maven : javax.servlet:javax.servlet-api:3.1.0
    @RequestMapping(value = ["/test"], method = [RequestMethod.GET])
    fun testFile(): String {
        return "testFile"
    }

    @RequestMapping("/greeting")
    fun greeting(@RequestParam(value = "name", required = false, defaultValue = "World") name: String, model: Model): String {
        model.addAttribute("name", name)
        return "greeting"
    }

    //文件上传相关代码
    @RequestMapping(value = "/upload")
    @ResponseBody
    fun upload(@RequestParam("test") file: MultipartFile): MyResponse<String> {
        var response = MyResponse(201, "失败", "文件为空")
        if (file.isEmpty) {
            return response
        }
        // 获取文件名
        val fileName = file.originalFilename
        logger.info("上传的文件名为：" + fileName!!)
        // 获取文件的后缀名
        val suffixName = fileName.substring(fileName.lastIndexOf("."))
        logger.info("上传的后缀名为：$suffixName")
        // 文件上传后的路径
        val filePath = "E://test//"
        // 解决中文问题，liunx下中文路径，图片显示问题
        // fileName = UUID.randomUUID() + suffixName;
        val dest = File(filePath + fileName)
        // 检测是否存在目录
        if (!dest.parentFile.exists()) {
            dest.parentFile.mkdirs()
        }

        try {
            file.transferTo(dest)
            response.code = 200
            response.msg = "上传成功"
            response.data = "成功,文件名:$fileName"
            return response
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        response.msg = "上传失败,出现异常"
        return response
    }

    //文件下载相关代码
//    @RequestMapping("/download")
//    fun downloadFile(request: /*org.apache.catalina.servlet4preview.http.*/HttpServletRequest, response: HttpServletResponse): String? {
//        val fileName = "FileUploadTests.java"
//        if (fileName != null) {
//            //当前是从该工程的WEB-INF//File//下获取文件(该目录可以在下面一行代码配置)然后下载到C:\\users\\downloads即本机的默认下载的目录
//            val realPath = request.getServletContext().getRealPath(
//                    "//WEB-INF//")
//            val file = File(realPath, fileName)
//            if (file.exists()) {
//                response.contentType = "application/force-download"// 设置强制下载不打开
//                response.addHeader("Content-Disposition",
//                        "attachment;fileName=$fileName")// 设置文件名
//                val buffer = ByteArray(1024)
//                var fis: FileInputStream? = null
//                var bis: BufferedInputStream? = null
//                try {
//                    fis = FileInputStream(file)
//                    bis = BufferedInputStream(fis!!)
//                    val os = response.outputStream
//                    var i = bis.read(buffer)
//                    while (i != -1) {
//                        os.write(buffer, 0, i)
//                        i = bis.read(buffer)
//                    }
//                    println("success")
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                } finally {
//                    if (bis != null) {
//                        try {
//                            bis.close()
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//
//                    }
//                    if (fis != null) {
//                        try {
//                            fis.close()
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//
//                    }
//                }
//            }
//        }
//        return null
//    }

    //多文件上传
//    @RequestMapping(value = "/batch/upload", method = [RequestMethod.POST])
//    @ResponseBody
//    fun handleFileUpload(request: HttpServletRequest): String {
//        val files = (request as MultipartHttpServletRequest)
//                .getFiles("file")
//        var file: MultipartFile? = null
//        var stream: BufferedOutputStream? = null
//        for (i in files.indices) {
//            file = files[i]
//            if (!file!!.isEmpty) {
//                try {
//                    val bytes = file.bytes
//                    stream = BufferedOutputStream(FileOutputStream(
//                            File(file.originalFilename!!)))
//                    stream.write(bytes)
//                    stream.close()
//
//                } catch (e: Exception) {
//                    stream = null
//                    return ("You failed to upload " + i + " => "
//                            + e.message)
//                }
//
//            } else {
//                return ("You failed to upload " + i
//                        + " because the file was empty.")
//            }
//        }
//        return "upload successful"
//    }

    companion object {

        private val logger = LoggerFactory.getLogger(FileController::class.java)
    }
}
