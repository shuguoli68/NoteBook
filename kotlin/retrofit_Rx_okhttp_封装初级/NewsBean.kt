package cn.sharelink.kotlinweb.model.entity


data class NewsBean(
    val code: Int,
    val msg: String,
    val data: Data
)

data class Data(
    val tech: List<Tech>,
    val auto: List<Auto>,
    val money: List<Money>,
    val sports: List<Sport>,
    val dy: List<Dy>,
    val war: List<War>,
    val ent: List<Ent>,
    val toutiao: List<Toutiao>
)

data class Toutiao(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)

data class PicInfo(
    val ref: Any,
    val width: Any,
    val url: String,
    val height: Any
)

data class War(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)

data class Sport(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)

data class Dy(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)

data class Tech(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)

data class Money(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)

data class Auto(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)

data class Ent(
    val liveInfo: Any,
    val tcount: Int,
    val picInfo: List<PicInfo>,
    val docid: String,
    val videoInfo: Any,
    val channel: String,
    val link: String,
    val source: String,
    val title: String,
    val type: String,
    val imgsrcFrom: Any,
    val imgsrc3gtype: Int,
    val unlikeReason: String,
    val digest: String,
    val typeid: String,
    val addata: Any,
    val tag: String,
    val category: String,
    val ptime: String
)