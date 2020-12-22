load("@io_bazel_rules_play_routes//play-routes:play-routes.bzl", "play_routes")

play_routes(
    name = "play-routes",
    srcs = ["routes"],
    include_play_imports = True,
    generate_reverse_router = True,
    visibility = ["//visibility:public"],
)

filegroup(
    name = "app_conf",
    visibility = ["//visibility:public"],
    srcs = ["application.conf"]
)
