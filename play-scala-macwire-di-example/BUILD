load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library", "scala_binary", "scala_test", "scala_library_suite")
load("@io_bazel_rules_play_routes//play-routes:play-routes.bzl", "play_routes")

scala_library(
    name = "play",
    visibility = ["//visibility:public"],
    exports = [
        "@third_party//3rdparty/jvm/com/typesafe/play:play_2_13",
        "@third_party//3rdparty/jvm/com/typesafe/play:play_json_2_13",
        "@third_party//3rdparty/jvm/com/typesafe/play:twirl_api_2_13",
        "@third_party//3rdparty/jvm/com/typesafe/play:play_ws_2_13",
        "@third_party//3rdparty/jvm/com/typesafe/play:filters_helpers_2_13",
        ],
)

scala_library(
    name = "macwire",
    visibility = ["//visibility:public"],
    exports = [
        "@third_party//3rdparty/jvm/com/softwaremill/macwire:macros_2_13",
        ],
)

