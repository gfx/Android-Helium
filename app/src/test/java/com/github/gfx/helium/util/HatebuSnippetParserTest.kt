package com.github.gfx.helium.util

import org.junit.Test
import org.junit.runner.RunWith

import android.support.test.runner.AndroidJUnit4

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*

@RunWith(AndroidJUnit4::class)
class HatebuSnippetParserTest {

    @Test
    @Throws(Exception::class)
    fun testParseSummaryWithImage() {
        val snippet = "<blockquote cite=\"http://honeshabri.hatenablog.com/entry/soap_anime\" title=\"なぜラノベ原作ヒロインは3分以内に脱ぐのか - 本しゃぶり\"><cite><img src=\"http://cdn-ak.favicon.st-hatena.com/?url=http%3A%2F%2Fhoneshabri.hatenablog.com%2F\" alt=\"\" /> <a href=\"http://honeshabri.hatenablog.com/entry/soap_anime\">なぜラノベ原作ヒロインは3分以内に脱ぐのか - 本しゃぶり</a></cite><p><a href=\"http://honeshabri.hatenablog.com/entry/soap_anime\"><img src=\"http://cdn-ak.b.st-hatena.com/entryimage/268444791-1444567710.jpg\" alt=\"なぜラノベ原作ヒロインは3分以内に脱ぐのか - 本しゃぶり\" title=\"なぜラノベ原作ヒロインは3分以内に脱ぐのか - 本しゃぶり\" class=\"entry-image\" /></a></p><p>2015-10-10 なぜラノベ原作ヒロインは3分以内に脱ぐのか 見たもの 2分28秒と2分32秒。 これが何の時間か分かるだろうか。これは2015年秋アニメである『落第騎士の英雄譚』と『学戦都市アスタリスク』で、主人公がヒロインの着替えに遭遇するまでにかかった時間である。 落第騎士の英雄譚 第1話 学戦都市アスタリスク 第1話 2015年秋アニメが1周しつつある今日このごろ。いつもなら「どれが一...</p><p><a href=\"http://b.hatena.ne.jp/entry/http://honeshabri.hatenablog.com/entry/soap_anime\"><img src=\"http://b.hatena.ne.jp/entry/image/http://honeshabri.hatenablog.com/entry/soap_anime\" alt=\"はてなブックマーク - なぜラノベ原作ヒロインは3分以内に脱ぐのか - 本しゃぶり\" title=\"はてなブックマーク - なぜラノベ原作ヒロインは3分以内に脱ぐのか - 本しゃぶり\" border=\"0\" style=\"border: none\" /></a> <a href=\"http://b.hatena.ne.jp/append?http://honeshabri.hatenablog.com/entry/soap_anime\"><img src=\"http://b.hatena.ne.jp/images/append.gif\" border=\"0\" alt=\"はてなブックマークに追加\" title=\"はてなブックマークに追加\" /></a></p></blockquote><p><img src=\"http://cdn1.www.st-hatena.com/users/so/sora_h/profile_s.gif\" class=\"profile-image\" alt=\"sora_h\" title=\"sora_h\" width=\"16\" height=\"16\" /> <a href=\"http://b.hatena.ne.jp/sora_h/20151012#bookmark-268444791\">sora_h</a> <a rel=\"tag\" class=\"user-tag\" href=\"http://b.hatena.ne.jp/sora_h/anime/\">anime</a>, <a rel=\"tag\" class=\"user-tag\" href=\"http://b.hatena.ne.jp/sora_h/ranobe/\">ranobe</a>, <a rel=\"tag\" class=\"user-tag\" href=\"http://b.hatena.ne.jp/sora_h/culture/\">culture</a> </p>"

        val summary = HatebuSnippetParser(snippet).parseSummary()

        assertThat(summary,
                `is`("2015-10-10 なぜラノベ原作ヒロインは3分以内に脱ぐのか 見たもの 2分28秒と2分32秒。 これが何の時間か分かるだろうか。これは2015年秋アニメである『落第騎士の英雄譚』と『学戦都市アスタリスク』で、主人公がヒロインの着替えに遭遇するまでにかかった時間である。 落第騎士の英雄譚 第1話 学戦都市アスタリスク 第1話 2015年秋アニメが1周しつつある今日このごろ。いつもなら「どれが一..."))
    }


    @Test
    @Throws(Exception::class)
    fun testParseSummaryWithoutImage() {
        val snippet = "<blockquote cite=\"https://github.com/nodejs/node/issues/3214#issuecomment-146706303\" title=\"WG: Considering a new HTTP WG · Issue #3214 · nodejs/node · GitHub\"><cite><img src=\"http://cdn-ak.favicon.st-hatena.com/?url=https%3A%2F%2Fgithub.com%2Fnodejs\" alt=\"\" /> <a href=\"https://github.com/nodejs/node/issues/3214#issuecomment-146706303\">WG: Considering a new HTTP WG · Issue #3214 · nodejs/node · GitHub</a></cite><p>I am considering the formation of a new working group to take responsibility for the HTTP and HTTPS submodule in the core lib. The HTTP WG would take responsibility for the http API, the http-parser d...</p><p><a href=\"http://b.hatena.ne.jp/entry/https://github.com/nodejs/node/issues/3214%23issuecomment-146706303\"><img src=\"http://b.hatena.ne.jp/entry/image/https://github.com/nodejs/node/issues/3214%23issuecomment-146706303\" alt=\"はてなブックマーク - WG: Considering a new HTTP WG · Issue #3214 · nodejs/node · GitHub\" title=\"はてなブックマーク - WG: Considering a new HTTP WG · Issue #3214 · nodejs/node · GitHub\" border=\"0\" style=\"border: none\" /></a> <a href=\"http://b.hatena.ne.jp/append?https://github.com/nodejs/node/issues/3214%23issuecomment-146706303\"><img src=\"http://b.hatena.ne.jp/images/append.gif\" border=\"0\" alt=\"はてなブックマークに追加\" title=\"はてなブックマークに追加\" /></a></p></blockquote><p><img src=\"http://cdn1.www.st-hatena.com/users/yo/yosuke_furukawa/profile_s.gif\" class=\"profile-image\" alt=\"yosuke_furukawa\" title=\"yosuke_furukawa\" width=\"16\" height=\"16\" /> <a href=\"http://b.hatena.ne.jp/yosuke_furukawa/20151012#bookmark-268549835\">yosuke_furukawa</a> httpモジュールがcoreから分離するかもー</p>"

        val summary = HatebuSnippetParser(snippet).parseSummary()

        assertThat(summary,
                `is`("I am considering the formation of a new working group to take responsibility for the HTTP and HTTPS submodule in the core lib. The HTTP WG would take responsibility for the http API, the http-parser d..."))
    }
}
