# Command feedback review evidence

These JavaFX scene snapshots support the fixes for issues #577, #578, and
#594. They render the actual MainWindow, command box, and reply after command
submission, without showing a window on the desktop. The before images use
master at `47f2065d`; the after images use the corresponding issue fix.

* #577: `add Tan -P 91234567`
* #578: `add E3 -e UPPER@X.COM`, then `add E13 -e upper@x.com`
* #594: `add Marker Test -t "Needs follow up"`

Each capture uses its own empty data directory. These images are review
artifacts held on a fork-only branch and are not part of the product PRs.
