load("@io_bazel_rules_scala//scala:scala.bzl", "scala_library", "scala_binary", "scala_test")

scala_library(
    name = "common",
    srcs = glob(["*.scala"]),
    visibility = ["//visibility:public"],
    deps = ["//:play"],
)