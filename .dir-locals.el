;;; .dir-locals.el --- Project configuration -*- lexical-binding: t; mode: emacs-lisp -*-

;;; Commentary:

;;; Code:
((nil .
      ;; Testing
      ((projectile-project-test-cmd . "clj -M:test/run")
         (projectile-project-test-dir . "test/")
         (projectile-project-test-suffix "_test")
      ;; Compile / Build
         (projectile-project-compilation-cmd . "clj -A:build")
         (compilation-read-command nil)
        ;; Run
          (projectile-project-run-cmd . "clj -M:build")
          ;; do not ask for compile command each time
         ))
 (clojure-mode . ((fill-column . 105)
                  (mode . auto-fill)
                  (indent-tabs-mode . nil))))

;;; .dir-locals.el ends here
